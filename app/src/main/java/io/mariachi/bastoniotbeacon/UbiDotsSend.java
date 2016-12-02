package io.mariachi.bastoniotbeacon;

import java.util.List;

/**
 * Created by antonio on 2/12/16.
 */

public class UbiDotsSend {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    static class Data{
        private String value;
        private int number;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
