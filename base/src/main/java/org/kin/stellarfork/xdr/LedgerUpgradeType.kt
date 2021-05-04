// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten
package org.kin.stellarfork.xdr

import java.io.IOException

// === xdr source ============================================================
//  enum LedgerUpgradeType
//  {
//      LEDGER_UPGRADE_VERSION = 1,
//      LEDGER_UPGRADE_BASE_FEE = 2,
//      LEDGER_UPGRADE_MAX_TX_SET_SIZE = 3
//  };
//  ===========================================================================
enum class LedgerUpgradeType(val value: Int) {
    LEDGER_UPGRADE_VERSION(1),
    LEDGER_UPGRADE_BASE_FEE(2),
    LEDGER_UPGRADE_MAX_TX_SET_SIZE(3);

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun decode(stream: XdrDataInputStream): LedgerUpgradeType {
            val value = stream.readInt()
            return when (value) {
                1 -> LEDGER_UPGRADE_VERSION
                2 -> LEDGER_UPGRADE_BASE_FEE
                3 -> LEDGER_UPGRADE_MAX_TX_SET_SIZE
                else -> throw RuntimeException("Unknown enum value: $value")
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun encode(stream: XdrDataOutputStream, value: LedgerUpgradeType) {
            stream.writeInt(value.value)
        }
    }

}
