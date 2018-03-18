package com.binarybricks.coiny.data.database.entities

import android.arch.persistence.room.*
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

/**
 * Created by Pragya Agrawal
 */
@Entity(indices = [(Index("watched_id", unique = true))])
@Parcelize
data class WatchedCoin(
    @Embedded
    val coin: Coin,
    var exchange: String,
    var fromCurrency: String,
    var purchaseQuantity: BigDecimal = BigDecimal.ZERO,
    @ColumnInfo(name = "watched_id") @PrimaryKey(autoGenerate = true) var idKey: Long = 0
) : Parcelable