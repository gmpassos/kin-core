// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten
package org.kin.stellarfork.xdr

import java.io.IOException

// === xdr source ============================================================
//  struct PathPaymentOp
//  {
//      Asset sendAsset; // asset we pay with
//      int64 sendMax;   // the maximum amount of sendAsset to
//                       // send (excluding fees).
//                       // The operation will fail if can't be met
//
//      AccountID destination; // recipient of the payment
//      Asset destAsset;       // what they end up with
//      int64 destAmount;      // amount they end up with
//
//      Asset path<5>; // additional hops it must go through to get there
//  };
//  ===========================================================================
class PathPaymentOp {
    var sendAsset: Asset? = null
    var sendMax: Int64? = null
    var destination: AccountID? = null
    var destAsset: Asset? = null
    var destAmount: Int64? = null
    var path: Array<Asset?> = arrayOfNulls(0)

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun encode(stream: XdrDataOutputStream, encodedPathPaymentOp: PathPaymentOp) {
            Asset.encode(stream, encodedPathPaymentOp.sendAsset!!)
            Int64.encode(stream, encodedPathPaymentOp.sendMax!!)
            AccountID.encode(stream, encodedPathPaymentOp.destination!!)
            Asset.encode(stream, encodedPathPaymentOp.destAsset!!)
            Int64.encode(stream, encodedPathPaymentOp.destAmount!!)
            val pathsize = encodedPathPaymentOp.path.size
            stream.writeInt(pathsize)
            for (i in 0 until pathsize) {
                Asset.encode(stream, encodedPathPaymentOp.path[i]!!)
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun decode(stream: XdrDataInputStream): PathPaymentOp {
            val decodedPathPaymentOp = PathPaymentOp()
            decodedPathPaymentOp.sendAsset = Asset.decode(stream)
            decodedPathPaymentOp.sendMax = Int64.decode(stream)
            decodedPathPaymentOp.destination = AccountID.decode(stream)
            decodedPathPaymentOp.destAsset = Asset.decode(stream)
            decodedPathPaymentOp.destAmount = Int64.decode(stream)
            val pathsize = stream.readInt()
            decodedPathPaymentOp.path = arrayOfNulls(pathsize)
            for (i in 0 until pathsize) {
                decodedPathPaymentOp.path[i] = Asset.decode(stream)
            }
            return decodedPathPaymentOp
        }
    }
}
