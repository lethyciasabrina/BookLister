# Book Lister

Book Lister is an app designed to help you manage your book list. It allows you to add, remove, and
organize your books in a simple and intuitive way.

## How It Works

<img src="result/Book-Lister-App.gif" alt="Demo" width="600" height="400">

## Features

* Add Books: Users can add new books to their list using the + menu icon.
* Edit Books: Users can edit a book's title and author by swiping from left to right on the book
  item.
* Remove Books: Users can remove books from their list by swiping from right to left on the book
  item.
* Search for Books: Users can search for books in their list in real-time using a SearchView.
* Mark as Read: Users can mark books as read using a checkbox on each book item.
* Filter Books by Status: Users can filter books based on their reading status using a Chip Group.
  Available filters include:
    - **All Books**: Displays all books in the list (Default Filter).
    - **Not Started**: Displays books with the "Not Started" status.
    - **In Progress**: Displays books that are currently being read.
    - **Stopped**: Displays books that were paused.
    - **Done**: Displays books marked as read.

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
    - SearchView
    - Chip Group
    - Bottom Sheet
* Swipe Functionality: Implemented using
  `RecyclerViewSwipeDecorator` (https://github.com/xabaras/RecyclerViewSwipeDecorator) for enhanced
  item swipe
  interactions.
* Toast for displaying messages
* View Binding for easier UI interactions

## TODO

- Refactor codebase to align with industry best practices: Improve code readability,
  maintainability, and scalability by applying principles such as:
    - SOLID principles for better object-oriented design.
    - Separation of concerns and modularization for a cleaner architecture.
    - Use of modern Android development patterns, like MVVM or MVI, and tools like ViewModel and
      LiveData.
    - Writing unit and integration tests to ensure code reliability and prevent bugs.

### License

```
The MIT License (MIT)

Copyright (c) 2025 Lethycia Sabrina Leal Santos

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```