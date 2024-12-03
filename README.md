# Book Lister

Book Lister is an app designed to help you manage your book list. It allows you to add, remove, and
organize your books in a simple and intuitive way.

## :camera_flash: Screenshots

<!-- You can add more screenshots here if you like -->
<img src="/result/home.png" width="260" alt=""> <img src="/result/new_book_toast.png" width="260" alt="">
<img src="/result/new_book.png" width="260" alt=""> <img src="/result/recyclerview.png" width="260" alt="">
<img src="/result/swipe_delete.png" width="260" alt=""> <img src="/result/swipe_delete_dialog.png" width="260" alt="">
<img src="/result/recyclerview2.png" width="260" alt=""> <img src="/result/searchview.png" width="260" alt="">

## Features

* Add Books: Users can add new books to their list, including title and author
* Edit Books: Users can edit the title or author of a book by clicking the pencil icon.
* Remove Books: Users can remove books from the list by swiping from right to left. Swipe
  functionality was implemented using the
  RecyclerViewSwipeDecorator (https://github.com/xabaras/RecyclerViewSwipeDecorator).
* Book Sorting: Allows the user to sort the list of books using the **drag and drop** functionality
  to improve organization.
* Search for Books: Users can search for books in their list using a SearchView. The feature filters
  the list dynamically by matching the query with book titles, providing an efficient way to locate
  specific books.

## Technologies

* Kotlin
* Activity for screen management
* ViewGroup
    - ConstraintLayout
* UI Components
    - TextView
    - EditText
    - Button
    - Floating Action Button
    - RecyclerView (with Drag and Drop support)
    - AlertDialog
    - SearchView (for dynamic filtering of book titles)
* Toast for displaying messages
* View Binding for easier UI interactions

### TODO

- Add login system: Allow users to log in to the app to customize their profile, including photo,
  description, and saving preferences.
- Implement reading status for books: Add an option to mark books as "not started," "in progress," "
  finished," or "stopped."
- Improve the layout with animations and a more modern design.
- Implement dark mode: Add a dark mode for better usability in low-light environments.

## Author

Lethycia Sabrina Leal Santos (follow me
on [Linkedin](https://www.linkedin.com/in/lethyciasabrinaleal/))
