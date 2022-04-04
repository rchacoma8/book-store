# Commerce Services - Technical Interview


## JSON structure

#### Post order examples:
``` http://localhost:8085/order/ ```
with body:

```
 {
    "order": [
       
        {"name": "14e05a52-eec7-4ff4-af1b-d6fd1cb90207", "quantity":1}
    ]
}
```

```
 {
    "order": [
       
        {"name": "14e05a52-eec7-4ff4-af1b-d6fd1cb90207", "quantity":2},
        {"name": "6fcf1723-e8a4-4e4a-b84b-f29d1f7393e6", "quantity":3}
    ]
}
```

#### Get all orders
``` http://localhost:8085/orders/ ```

#### Get all stock
``` http://localhost:8085/books_stock/all ```

## Improvements

- Change response to ResponseEntity. Include HTTP response in Future<>.
- Move files by feature to folders: bookstock, bookorder, bookorderitem.
- Prevent concurrency error - while updating stock, another thread can process an order (see if there is enough stock), but later find out that there is not enough stock.
- Extract async function to process the order. I believe it needs to be in a different file (Service) from the one calling.
- Add unit tests, database tests, concurrency tests and so.


---


## Dev part

### Problem:

We are an online book store. We receive book orders from customers and process them.

### Features

- **Create a new Order**:
  - The application receives orders in a JSON format through an HTTP API endpoint (POST).
  - Orders contain a list of books and the quantity.
  - Before registering the order, the system should check if there's enough stock to fulfill the order (`import.sql` will set the initial stock).
  - If one of the books in the order does not have enough stock we will reject the entire order.
  - After stock validation, the order is marked as a success, and it would return a Unique Order Identifier to the caller of the HTTP API endpoint.
  - If the order was processed we need to update available stock, taking into consideration:
    - Updating stock should not be a blocker for replying to the customer.
    - If the process of updating stock fails, should not cause an error in order processing.

- **Retrieve Orders**:
  - The application has an endpoint to extract a list of existing orders. So that we can run "curl localhost:8080/orders/" and get a list of them

### Required:

- Resolution needs to be fully in English
- You need to use Java 11
- You are provided with a scaffold, fork or create a public repository with your solution. Once the code is complete, reply to your hiring person of contact.
- We expect you to implement tests for the requested functionalities. You decide the scope.

### How to run

Building
```shell
$ ./mvnw compile
```

Test
```shell
$ ./mvnw test
```

Start the application

```shell
$ ./mvnw spring-boot:run
```

Getting current stock for a given book 

```shell
$ curl localhost:8080/books_stock/ae1666d6-6100-4ef0-9037-b45dd0d5bb0e
{"id":"ae1666d6-6100-4ef0-9037-b45dd0d5bb0e","name":"adipisicing culpa Lorem laboris adipisicing","quantity":0}
```
