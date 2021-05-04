// Automatically generated by xdrgen 
// DO NOT EDIT or your changes may be overwritten
package org.kin.stellarfork.xdr

import java.io.IOException

// === xdr source ============================================================
//  struct SCPQuorumSet
//  {
//      uint32 threshold;
//      PublicKey validators<>;
//      SCPQuorumSet innerSets<>;
//  };
//  ===========================================================================
class SCPQuorumSet {
    var threshold: Uint32? = null
    var validators: Array<PublicKey?> = arrayOfNulls(0)
    var innerSets: Array<SCPQuorumSet?> = arrayOfNulls(0)

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun encode(stream: XdrDataOutputStream, encodedSCPQuorumSet: SCPQuorumSet?) {
            Uint32.encode(stream, encodedSCPQuorumSet!!.threshold!!)
            val validatorssize = encodedSCPQuorumSet.validators.size
            stream.writeInt(validatorssize)
            for (i in 0 until validatorssize) {
                PublicKey.encode(
                    stream,
                    encodedSCPQuorumSet.validators[i]!!
                )
            }
            val innerSetssize = encodedSCPQuorumSet.innerSets.size
            stream.writeInt(innerSetssize)
            for (i in 0 until innerSetssize) {
                encode(stream, encodedSCPQuorumSet.innerSets[i])
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun decode(stream: XdrDataInputStream): SCPQuorumSet {
            val decodedSCPQuorumSet = SCPQuorumSet()
            decodedSCPQuorumSet.threshold = Uint32.decode(stream)
            val validatorssize = stream.readInt()
            decodedSCPQuorumSet.validators = arrayOfNulls(validatorssize)
            for (i in 0 until validatorssize) {
                decodedSCPQuorumSet.validators[i] =
                    PublicKey.decode(stream)
            }
            val innerSetssize = stream.readInt()
            decodedSCPQuorumSet.innerSets = arrayOfNulls(innerSetssize)
            for (i in 0 until innerSetssize) {
                decodedSCPQuorumSet.innerSets[i] = decode(stream)
            }
            return decodedSCPQuorumSet
        }
    }
}
