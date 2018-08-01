package com.zukkadev.it.flickrtourist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.zukkadev.it.flickrtourist.data.AppDatabase;
import com.zukkadev.it.flickrtourist.model.Pin;

import java.util.List;

public class PinViewModel extends AndroidViewModel {

    private LiveData<List<Pin>> restoredPin;

    public PinViewModel(@NonNull Application application) {
        super(application);
        AppDatabase mDb = AppDatabase.getInstance(this.getApplication());
        restoredPin = mDb.pinsDao().getAllPins();
    }

    public LiveData<List<Pin>> getRestoredPin() {
        return restoredPin;
    }


}
