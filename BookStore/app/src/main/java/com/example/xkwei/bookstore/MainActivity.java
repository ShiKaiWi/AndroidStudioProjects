package com.example.xkwei.bookstore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private IBookManager mRemoteBookManager;
    private RecyclerView mBookListView;
    private Button mAddBookButton;
    private MyAdapter mAdapter;

    private Handler mHandler = new Handler();
    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            final Book book = newBook;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"got a new Book"+book.bookName);
//                    Toast.makeText(MainActivity.this,"Got a new Book#"+book.bookName,Toast.LENGTH_SHORT).show();
                    updateBookList();
                }
            });
        }
    };
    private class BookItemHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        public BookItemHolder(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.book_item);
        }

        public void onBind(String info){
            mTextView.setText(info);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<BookItemHolder>{
        private List<Book> mBooks;

        public void setBooks(List<Book> books){
            mBooks = books;
        }
        public MyAdapter(){
            mBooks = null;
        }
        public BookItemHolder onCreateViewHolder(ViewGroup container, int viewType){
            LayoutInflater lif = LayoutInflater.from(MainActivity.this);
            return new BookItemHolder(lif.inflate(R.layout.book_item,container,false));
        }
        public void onBindViewHolder(BookItemHolder holder,int position){
            holder.onBind(mBooks.get(position).bookName);
        }
        public int getItemCount(){
            return mBooks==null?0:mBooks.size();
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"connected with the service");
            mRemoteBookManager = IBookManager.Stub.asInterface(service);
            updateBookList();
            try{
                mRemoteBookManager.registerOnNewBookArrivedListener(mIOnNewBookArrivedListener);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookListView = (RecyclerView)findViewById(R.id.book_list);
        mBookListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter();
        mBookListView.setAdapter(mAdapter);
        mAddBookButton = (Button)findViewById(R.id.add_book);
        mAddBookButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    mRemoteBookManager.addBook(Book.newBook());

                }catch(RemoteException e){
                    e.printStackTrace();

                }
                updateBookList();
            }
        });
        Intent i = new Intent(this,BookManagerService.class);
        bindService(i,mConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateBookList() {
        try {
            if(mRemoteBookManager!=null) {
                mAdapter.setBooks(mRemoteBookManager.getBookList());
                mAdapter.notifyDataSetChanged();
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        try {
            mRemoteBookManager.unregisterOnNewBookArrivedListener(mIOnNewBookArrivedListener);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
