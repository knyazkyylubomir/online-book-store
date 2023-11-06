# Online Book Store
The project is a comprehensive API solution for a web store of books. It has all required endpoints such as adding a book to the shopping cart or finding a book by book id or category id, along with the authentication endpoints.

![](https://www.loom.com/share/630ad52642ea436fae17a26dceb599df?sid=201abbaa-c895-49f2-8585-b6fe46d5250a)
## Technologies
Technologies used in my project:
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [Spring Security](https://spring.io/projects/spring-security)
* [Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
* [Swagger](https://springdoc.org/)
## Installation
Requirements
1) [JDK 17 or higher](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2) [Maven](https://maven.apache.org/download.cgi)
3) [Docker](https://www.docker.com/products/docker-desktop/)

1. Download the zip archive from the repository.
![](https://i.imgur.com/UklTYVU.png)
2. Extract the downloaded archive where you want.
3. Enable "File name extensions" to create a .env file. (I use Windows 11)
![](https://i.imgur.com/s725kqh.png)
4. Create a .txt file and rename it to .env. We need .env to establish a DB connection and expose an external docker connection to localhost.
![](https://i.imgur.com/cb4HhWo.png)
5. Open it with a notepad.
6. Set up .env file
![](https://i.imgur.com/e08d001.png)
7. Open a terminal within the current folder. Press and hold "Shift" and press on the empty field right mouse button.
![](https://i.imgur.com/CwRcIcY.png)
8. To build and start the application, write this command "docker-compose up".
![](https://i.imgur.com/YH74BQD.png)
9. After several attempts to start the spring application, it must start. 
![](https://i.imgur.com/p5b050E.png)
10. You can now utilize the application
![](https://i.imgur.com/fogO82f.png)
### Troubleshooting
* If you received this error while docker-compose is building.
![](https://i.imgur.com/JalNbPz.png)
Try to build the application. Execute the command "mvn clean package". After it is executed, try "docker-compose up" once again.
## Controllers functionalities
## I. Controllers available for role user 
Let’s begin with the authentication controller. Within it, you can register and log in.
### Authentication controller
#### Register
![](https://i.imgur.com/SeH2Kpn.png)
![](https://i.imgur.com/FJmtrwP.png)
![](https://i.imgur.com/4rCLtp4.png)
#### Login
We are trying to log in with a recently created user.
![](https://i.imgur.com/SJkdOZE.png)
![](https://i.imgur.com/ZpWsT1M.png)
As you can see as a response body, we are receiving JWT Token which is required to be used for all other endpoints. You can copy and paste this token via this button.
![](https://i.imgur.com/iLkyrVL.png)
And paste here to be able to use other endpoints since those endpoints require to be logged in.
![](https://i.imgur.com/nKoZ9IQ.png)
From this point, you are now eligible to use other endpoints.
### Books Controller
#### Receive all books
As an authenticated user, you might want to see a list of books that are available on the website. You can see it through this endpoint.
![](https://i.imgur.com/A56DAy4.png)
As you can see, we can configure the page size and sorting parameters (I’m going to show how that works later*)
![](https://i.imgur.com/OENoTXW.png)
#### Receive a book by id
You can get a book by its internal identifier.
![](https://i.imgur.com/h7fWNAr.png)
![](https://i.imgur.com/5iTailQ.png)
#### Receive books by search parameters
As with many web stores, you are able to find books by filter parameters. Currently, you can search books by price and their authors.

(This picture represents internal DB fullness)
![](https://i.imgur.com/N5WOFns.png)
Here are the search parameters to find books that correspond to filters (along with pageable)
![](https://i.imgur.com/Z7xcmLY.png)
As you can see, we found two books with prices 20 and 30, and author's name "Author" 
![](https://i.imgur.com/ZLJjQQs.png)
### Categories Controller
Maybe when you search a book, you want to find the particular book or books by their category (genre). You can utilize the next endpoints.
#### Receive all categories
![](https://i.imgur.com/O9Mg03v.png)
![](https://i.imgur.com/0aRapKi.png)
#### Receive a category by id
You can get a category by its internal identifier.
![](https://i.imgur.com/OsTO8tC.png)
![](https://i.imgur.com/cxfpSnN.png)
#### Receive all books by category id
Within this controller, you can search books with a category filter. You need to provide a category internal identifier. Let me show you.
![](https://i.imgur.com/9d271qt.png)
As you can see, we received a list of books where the category identifier equals 1. (Notice, all books received don't have a category field. So in my DB only books 1, 2, and 7 have categories with id 1.)
![](https://i.imgur.com/sGMqH6o.png)
### Shopping Cart Controller
Since you choose a book or books you want to read, you might want to buy them. For this purpose, you need to add a book to a shopping cart.
#### Add a book to a shopping cart
Here you need to specify a book by its identifier and set quantity.
![](https://i.imgur.com/tYYt8zh.png)
Notice, you've received only status code 202 as a response.
![](https://i.imgur.com/oRWt3Mw.png)
#### Receive a shopping cart with cart-items
Since you add a book or books, you need to see whether a book is correct.
![](https://i.imgur.com/ncfMXXT.png)
Response body with cart-items.
![](https://i.imgur.com/vo7dHub.png)
#### Update quantity of a cart-item by id
If you need to update the quantity of some cart items, you can utilize this endpoint. Notice that the quantity of a cart item will be rewritten.

If you remember within "Add a book to a shopping cart" endpoint, I've put the book with quantity 2. So now I'm going to update the quantity to 5.
![](https://i.imgur.com/klppEfi.png)
As a response, we received status code 202.
![](https://i.imgur.com/GFQHVGK.png)
Let's check our shopping cart.
![](https://i.imgur.com/GvYzCqc.png)
As you see now, the quantity equals 5.
#### Delete a cart-item by id
If you decide you don't need the book, you can remove it from a shopping cart by this endpoint.
![](https://i.imgur.com/Wvc0QUw.png)
As a response, we received status code 204
![](https://i.imgur.com/hZCmmgv.png0)
Let's ensure that we deleted the cart-item
![](https://i.imgur.com/7GhAwOJ.png)
### Order Controller
So your shopping cart is not empty, so you can buy chosen books.
#### Place an order
In request body, you need to just put the required address.
![](https://i.imgur.com/itEjAnL.png)
As a response, we received status code 202.
![](https://i.imgur.com/JqC14cA.png)
#### Receive order history
Since we placed the order, you can now check its status along with previous orders. Utilize this endpoint
![](https://i.imgur.com/pkIplno.png)
You received a list of orders.
![](https://i.imgur.com/QAaqCNI.png)
For now, our order has the status "PENDING". (Notice, the status can be changed by the admin user)
#### Receive all items of the order
If you want to receive the items only of a particular order, use this endpoint.
![](https://i.imgur.com/lBzvQSU.png)
![](https://i.imgur.com/senqtpA.png)
#### Receive an item of the order
You can receive a particular item of the order.
![](https://i.imgur.com/MqE7ukc.png)
![](https://i.imgur.com/hGeAhDM.png)
## II. Controllers available for role admin
Admins are eligible to use all endpoints, so within this topic, we're going to take a glance only at the controllers that are available for role admin.
### Book Controller
#### Create a new book
Press "Schema" to check which fields are required to be not empty.
![](https://i.imgur.com/Dq0XDFb.png)
Red ✱ (it is supposed to be red) is telling you that fields are required.
![](https://i.imgur.com/Bvp0mdX.png)
![](https://i.imgur.com/M3nM601.png)
So we added a book to DB.
![](https://i.imgur.com/lOgkQR2.png)
#### Update a book by id
I'm going to update the recently added book with identifier 11.

Notice you can fully update a book entity or partially update it.
![](https://i.imgur.com/f7Iz8Zi.png)
![](https://i.imgur.com/jQuj5Ks.png)
#### Delete a book by id
So if a book needs to be deleted, use this endpoint.

I'm going to delete the book with identifier 11.
![](https://i.imgur.com/jgUaZTm.png)
As a response body, we received status code 204.
![](https://i.imgur.com/17sZUS7.png)
Let's check if the book is deleted.
![](https://i.imgur.com/pjpyQUa.png)
Yes, we received status code 404 (Not found).
![](https://i.imgur.com/9I0peW3.png)
### Category Controller
#### Create a new category
![](https://i.imgur.com/MTa6DUn.png)
![](https://i.imgur.com/jnAvFrZ.png)
#### Update a category by id
I'm going to update the description of the recently added category with identifier 5.

Notice to update a category you need to provide all fields otherwise, you receive status code 400 (Bad request)
![](https://i.imgur.com/B6lvSq6.png)
![](https://i.imgur.com/objZdbZ.png)
#### Delete a category by id
You can delete a category by its identifier

I'm going to delete the category with identifier 5.
![](https://i.imgur.com/BbPOvXE.png)
As a response, we received status 204(No content), so the category doesn't exist in DB anymore.
![](https://i.imgur.com/zoUGOEO.png)
### Order Controller
#### Update order status
The current user (admin) placed an order and its status is "PENDING". I want to change the status to "DELIVERED". Let's do this.
![](https://i.imgur.com/uW5CzCV.png)
There are only three statuses available to use:
* PENDING,
* DELIVERED,
* COMPLETED

![](https://i.imgur.com/lpTUg28.png)

Notice, if you use other statuses or misspelling of a word, you receive: 
```json
{
  "timestamp": "2023-10-24T12:31:56.222608112",
  "status": "BAD_REQUEST",
  "errors": [
    "The status is not correct! Make sure the status is correctly written"
  ]
}
```
Now the status is updated.
![](https://i.imgur.com/MsalJJ5.png)
Let's check it out.
![](https://i.imgur.com/9Pea9oh.png)
Thus, we updated the status of the order.
## Pageable
So with pageable, you can set how items from DB will be displayed. Typical it looks:
```json
{
  "page": 0,
  "size": 20,
  "sort": [
    "price: DESC"
  ]
}
```
So with those parameters, we can show 20 entities per page and each entity sorts from higher to lower price since I put the parameter "DESC".

We can also sort entities by author in alphabetic order. It will look like this:
```json
{
  "sort": [
    "author: ASC"
  ]
}
```
With the parameter "ASC" I explicitly show that I'm searching in alphabetic order (notice you can either put "ASC" or not put it since it by default searches in alphabetic order)
## Challenges
While developing the project, I faced some challenges. Firstly, MapStruct. As you can see, MapStruct creates every object within the service layer. So I needed to map a list of some dto to a list of some dto. I overcame it by creating mappers that create an entity from a dto and a response dto from an entity.

![](https://i.imgur.com/RJGjxRZ.png)

Under the hood, MapStruct understands to map a list of dto it needs to use one of the main mappers like toEntity or toDto.

Secondly, JWT Token. Pay enough attention while configuring JWT Token since it's a multilayer service. So always debug and check how a service works step by step. Thus, you decrease the chance of errors and development time.
