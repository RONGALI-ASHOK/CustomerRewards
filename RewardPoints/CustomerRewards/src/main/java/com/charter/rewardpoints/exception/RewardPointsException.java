package com.charter.rewardpoints.exception;

public class RewardPointsException extends RuntimeException{

    private static final long serialVersionUID = 1;
    public RewardPointsException(String message) {
        super(message);
    }

    public RewardPointsException(String message, Throwable cause){
        super(message,cause);
    }
}
