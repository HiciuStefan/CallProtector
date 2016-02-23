package com.transapps.callprotector.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hiciu on 2/16/2016.
 */
public class ModelNumberQuerry {
    @SerializedName ("status")
    public boolean status;
    @SerializedName ("records")
    public List<Records> theRecords;
}
