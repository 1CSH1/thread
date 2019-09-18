package com.liebrother.thread._0001_blocking_nonblocking;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author James
 * @date 7/17/2019
 */
public class BlockingTest {

    public static void main(String[] args) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        String dateOfBirth = sdf.format(date);
        System.out.println(dateOfBirth);
    }

}
