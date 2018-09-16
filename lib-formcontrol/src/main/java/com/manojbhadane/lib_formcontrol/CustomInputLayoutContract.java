package com.manojbhadane.lib_formcontrol;

import java.util.ArrayList;

public interface CustomInputLayoutContract {

    public interface View {

    }

    public interface Presenter {
        public ArrayList<String> getTimeSlots();

        public String pad(int input);

        public boolean isValidEmail(CharSequence email);
    }
}
