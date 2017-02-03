// IOnNewBookArrivedListener.aidl
package com.example.xkwei.bookstore;

import com.example.xkwei.bookstore.Book;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
   void onNewBookArrived(in Book newBook);
}
