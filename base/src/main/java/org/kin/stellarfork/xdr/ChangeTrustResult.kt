// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten
package org.kin.stellarfork.xdr

import java.io.IOException

// === xdr source ============================================================
//  union ChangeTrustResult switch (ChangeTrustResultCode code)
//  {
//  case CHANGE_TRUST_SUCCESS:
//      void;
//  default:
//      void;
//  };
//  ===========================================================================
class ChangeTrustResult {
    var discriminant: ChangeTrustResultCode? = null

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun encode(
            stream: XdrDataOutputStream,
            encodedChangeTrustResult: ChangeTrustResult
        ) {
            stream.writeInt(encodedChangeTrustResult.discriminant!!.value)
            when (encodedChangeTrustResult.discriminant) {
                ChangeTrustResultCode.CHANGE_TRUST_SUCCESS -> {
                }
                else -> {
                }
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun decode(stream: XdrDataInputStream): ChangeTrustResult {
            val decodedChangeTrustResult = ChangeTrustResult()
            val discriminant = ChangeTrustResultCode.decode(stream)
            decodedChangeTrustResult.discriminant = discriminant
            when (decodedChangeTrustResult.discriminant) {
                ChangeTrustResultCode.CHANGE_TRUST_SUCCESS -> {
                }
                else -> {
                }
            }
            return decodedChangeTrustResult
        }
    }
}
