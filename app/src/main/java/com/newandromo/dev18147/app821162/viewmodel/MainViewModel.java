package com.newandromo.dev18147.app821162.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.db.DataRepository;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<Integer> mUnreadCount;
    private final LiveData<Integer> mUnreadBookmarkedCount;

    public MainViewModel(@NonNull Application application) {
        super(application);
        DataRepository repo = ((MyApplication) application).getRepository();
        mUnreadCount = repo.getUnreadEntriesCount();
        mUnreadBookmarkedCount = repo.getUnreadBookmarkedEntriesCount();
    }

    public LiveData<Integer> getUnreadCount() {
        return mUnreadCount;
    }

    public LiveData<Integer> getUnreadBookmarkedCount() {
        return mUnreadBookmarkedCount;
    }
}
