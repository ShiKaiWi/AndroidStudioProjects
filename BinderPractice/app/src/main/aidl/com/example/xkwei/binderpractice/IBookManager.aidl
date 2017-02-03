package com.example.xkwei.binderpractice;

import com.example.xkwei.binderpractice.Book;
import com.example.xkwei.binderpractice.IOnNewBookArrivedListener;

interface IBookManager {
     List<Book> getBookList();
     void addBook(in Book book);
     void registerListener(IOnNewBookArrivedListener listener);
     void unregisterListener(IOnNewBookArrivedListener listener);
}