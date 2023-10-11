INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (3, 'Title3', 'Author3', '123456789(3)', 96.99, 'Descr.3', 'Image3');
INSERT INTO categories (id, name, description)
VALUES (3, 'Test name for update', 'Test name descr. for update');
INSERT INTO books_categories (book_id, category_id)
VALUES (3, 1);
