// IBookManager.aidl
package com.example.xkwei.bookstore;

import com.example.xkwei.bookstore.Book;
import com.example.xkwei.bookstore.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book newBook);
    void registerOnNewBookArrivedListener(IOnNewBookArrivedListener listener);
    void unregisterOnNewBookArrivedListener(IOnNewBookArrivedListener listener);
}
