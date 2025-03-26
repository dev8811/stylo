package com.Stylo.stylo.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetProductData {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("products")
    @Expose
    var productData: List<FetchProductData>? = null

    class FetchProductData {
        @SerializedName("subcategoryid")
        @Expose
        var subcategoryid: String? = null

        @SerializedName("productid")
        @Expose
        var productid: String? = null

        @SerializedName("productimage")
        @Expose
        var productimage: String? = null

        @SerializedName("productname")
        @Expose
        var productname: String? = null

        @SerializedName("originalprice")
        @Expose
        var originalprice: String? = null

        @SerializedName("discount")
        @Expose
        var discount: String? = null
    }
}
