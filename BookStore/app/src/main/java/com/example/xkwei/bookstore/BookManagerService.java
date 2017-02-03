package com.example.xkwei.bookstore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BookManagerService extends Service {
    private static final String TAG = "BMS";
    private ArrayList<IOnNewBookArrivedListener> mIOnNewBookArrivedListeners = new ArrayList<>();
    private ArrayList<Book> mBooks = new ArrayList<>();

    private Binder mBinder = new IBookManager.Stub(){

        @Override
        public List<Book> getBookList(){
            Log.i(TAG,"sending "+mBooks.size()+" books to the client");
            SystemClock.sleep(50);
            return mBooks;
        }

        @Override
        public void addBook(Book newBook){
            Log.i(TAG,"add a book called"+newBook.bookName);
            mBooks.add(newBook);
        }

        @Override
        public void registerOnNewBookArrivedListener(IOnNewBookArrivedListener listener){
            for(IOnNewBookArrivedListener lis :mIOnNewBookArrivedListeners){
                if(lis==listener){
                    return;
                }
            }
            mIOnNewBookArrivedListeners.add(listener);
        }

        @Override
        public void unregisterOnNewBookArrivedListener(IOnNewBookArrivedListener listener){
            for(int i=0;i<mIOnNewBookArrivedListeners.size();i++){
                if(mIOnNewBookArrivedListeners.get(i)==listener){
                    mIOnNewBookArrivedListeners.remove(i);
                    return;
                }
            }
        }
    };
    public BookManagerService() {
        mBooks.add(new Book(1,"Android Arts"));
        mBooks.add(new Book(2,"Introduction to Algorithms"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.i(TAG,"onBind");
        return mBinder;
    }

    private void onNewBookArrived(Book newBook) throws RemoteException{
        mBooks.add(newBook);
        for(IOnNewBookArrivedListener listener:mIOnNewBookArrivedListeners){
            if(listener!=null){
                listener.onNewBookArrived(newBook);
            }
        }
    }
    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            // do background processing here.....
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBooks.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                }catch(RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
