package org.example.game;

import java.io.Serializable;

public class Player implements Serializable {
        private String name;
        private char piece;
        public Player(String name,char piece){
            this.name=name;
            this.piece=piece;
        }
        public String getName(){
            return name;
        }
        public char getPiece(){
            return piece;
        }
}
