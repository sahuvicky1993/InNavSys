package com.example.joelwasserman.androidbletutorial;

public class Book {

        private int RSSI;
        private String BdAdrr;
        private int loc;

        public Book(){}

        public Book(String BdAddr,int RSSI, int loc) {
            super();
            this.BdAdrr = BdAddr;
            this.RSSI = RSSI;
            this.loc  = loc;
        }

        //getters & setters

        @Override
        public String toString() {
            return "Book [BdAddr=" + BdAdrr + ", RSSI=" + RSSI + ", loc=" + loc
                    + "]";
        }

        public String getBdAdrr(){ return BdAdrr;}
        public int getRSSI(){ return RSSI;}
        public int getLoc(){ return  loc;}


}
