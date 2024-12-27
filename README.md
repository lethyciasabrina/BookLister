# Book Lister

Book Lister is an app designed to help you manage your book list. It allows you to add, remove, and
organize your books in a simple and intuitive way.

## How It Works

<!-- Soon ... -->

## Features

* Add Books: Users can add new books to their list using the + menu icon.
* Edit Books: Users can edit a book's title and author by swiping from left to right on the book
  item.
* Remove Books: Users can remove books from their list by swiping from right to left on the book
  item. Swipe functionality was implemented using the
  RecyclerViewSwipeDecorator (https://github.com/xabaras/RecyclerViewSwipeDecorator).
* Search for Books: Users can search for books in their list in real-time using a SearchView.
* Mark as Read: Users can mark books as read using a checkbox on each book item.

## Technologies

* Kotlin
* Activity for screen management
* ViewGroup
    - ConstraintLayout
* UI components
    - TextView
    - EditText
    - Button
    - CheckBox
    - RecyclerView
    - AlertDialog
    - SearchView
* Swipe Functionality: Implemented using `RecyclerViewSwipeDecorator` for enhanced item swipe
  interactions.
* Toast for displaying messages
* View Binding for easier UI interactions

### TODO

- Implement reading status for books: Use a ChipGroup to let users mark books as "Not Started," "In
  Progress," "Finished," or "Stopped."
- Enhance the search functionality: Filter books using the SearchView based on the selected reading
  status from the ChipGroup.
- Refactor codebase to align with industry best practices: Improve code readability,
  maintainability, and scalability by applying principles such as:
    - SOLID principles for better object-oriented design.
    - Separation of concerns and modularization for a cleaner architecture.
    - Use of modern Android development patterns, like MVVM or MVI, and tools like ViewModel and
      LiveData.
    - Writing unit and integration tests to ensure code reliability and prevent bugs.

## Author

Lethycia Sabrina Leal Santos (follow me
on [Linkedin](https://www.linkedin.com/in/lethyciasabrinaleal/))
