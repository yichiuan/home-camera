package com.yichiuan.homecamera;


import android.content.Context;
import android.support.annotation.NonNull;

import com.yichiuan.homecamera.data.UserRepository;

public class Injection {
    public static UserRepository provideTasksRepository(@NonNull Context context) {
        return UserRepository.getInstance(context);
    }
}
