package com.transapps.callprotector.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hiciu on 2/16/2016.
 */
public class ModelNumberQuerry {
    @SerializedName ("status")
    boolean status;
    @SerializedName ("records")
    List<Records> theRecords;
}
