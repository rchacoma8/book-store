package com.adobe.bookstore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Service
public class BookOrderResource {

    private BookOrderRepository bookOrderRepository;

    @Autowired
    public BookOrderResource(BookOrderRepository bookOrderRepository) {
        this.bookOrderRepository = bookOrderRepository;
    }

    @Autowired
    private BookStockRepository bookStockRepository;

    @Autowired
    private BookOrderItemRepository bookOrderItemRepository;

    @RequestMapping("/orders_without_http_status/")
    @ResponseBody
    @GetMapping
    public HashMap<String, List<BookOrder>>  getBookOrdersWithoutHttpStatus() {
        HashMap<String, List<BookOrder>> map = new HashMap<>();
        
        List<BookOrder> orders = this.bookOrderRepository.findAll();
        map.put("orders", orders);

        return map;
    }

    @RequestMapping("/orders/")
    @ResponseBody
    @GetMapping
    public ResponseEntity<HashMap<String, List<BookOrder>>>  getBookOrders() {
        HashMap<String, List<BookOrder>> map = new HashMap<>();
        
        List<BookOrder> orders = this.bookOrderRepository.findAll();
        map.put("orders", orders);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(
        value = "/order/", 
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces =  MediaType.APPLICATION_JSON_VALUE
    )
    public Future<HashMap<String, String>> asyncOrderProcess(@RequestBody HashMap<String, List<HashMap<String, String>>> newBookOrder) {

        //Assumptions: quantity is greater than 0
        // Option 1: Cost O(2n), first run the list and check, second create entries
        // Option 2: Cost O(n), check and create entry, if it doesn't exist, rollback to previous state. Accessing the database is more costly.

        boolean missingStock = false;
        String errorMessage = "";
        String statusCode = "";

        //Assume there are not two items referencing the same Book (or BookStock in the project)
        int orderSize = newBookOrder.get("order").size();
        int count = 0;

        while(!missingStock && count < orderSize){

            HashMap<String, String> requiredBook = newBookOrder.get("order").get(count);
            Optional < BookStock > optional = bookStockRepository.findById(requiredBook.get("name"));

            if (optional.isPresent()) {
                int solicitedQuantity = Integer.parseInt(requiredBook.get("quantity"));

                if( !(solicitedQuantity > 0 && solicitedQuantity <= optional.get().getQuantity()) ){
                    missingStock = true;
                    errorMessage = "Not enough stock available";
                    statusCode = "452";
                }

            } else {
                missingStock = true;
                errorMessage = "Book not found";
                statusCode = "404";
            }

            count++;
        }

        if(missingStock){
            HashMap<String, String> xmap = new HashMap<>();
            xmap.put("error", errorMessage);
            xmap.put("status", statusCode);
            return new AsyncResult<HashMap<String, String>>(xmap);
        }

        // There is enough stock and we can proceed with the order
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOrderDate(new Date());
        bookOrder.setSuccess(true);

        UUID generatedUUID = null;
        generatedUUID =  bookOrderRepository.save(bookOrder).getId();

        proccessOrder(newBookOrder.get("order"), bookOrder);
        
        // Run a task specified by a Runnable Object asynchronously.
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000); //Sleep 10 seconds, se we can check it is a non-blocking process.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        
        
                newBookOrder.get("order").forEach(item -> {
                    int solicitedQuantity = Integer.parseInt(item.get("quantity"));
        
                    Optional<BookStock> bookStock = bookStockRepository.findById(item.get("name"));
                    if (bookStock.isPresent()) {
        
                        if(bookStock.get().getQuantity() - solicitedQuantity < 0){
                            bookOrder.setSuccess(false);
                            bookOrderRepository.save(bookOrder);
                        }
                        else {
                            bookStock.get().setQuantity( bookStock.get().getQuantity() - solicitedQuantity);
                            bookStockRepository.save(bookStock.get());
                        }
                        
                    }
                });
            }
        });
            
        HashMap<String, String> xmap = new HashMap<>();
        xmap.put("order_id", generatedUUID.toString());
        xmap.put("status", "200");

        return new AsyncResult<HashMap<String, String>>(xmap);
    }

    void proccessOrder(List<HashMap<String, String>> listorder, BookOrder bookOrder){
        listorder.forEach(item -> {
            int solicitedQuantity = Integer.parseInt(item.get("quantity"));

            Optional<BookStock> bookStock = bookStockRepository.findById(item.get("name"));
            if (bookStock.isPresent()) {
                BookOrderItem bookOrderItem = new BookOrderItem(bookOrder, bookStock.get(), solicitedQuantity);
                bookOrderItemRepository.save(bookOrderItem);
            }
        });
    }

    @Async
    void updateStock(List<HashMap<String, String>> listorder, BookOrder bookOrder){

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        listorder.forEach(item -> {
            int solicitedQuantity = Integer.parseInt(item.get("quantity"));

            Optional<BookStock> bookStock = bookStockRepository.findById(item.get("name"));
            if (bookStock.isPresent()) {

                bookStock.get().setQuantity( bookStock.get().getQuantity() - solicitedQuantity);
                bookStockRepository.save(bookStock.get());
            }
        });
    }

}
