package com.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;

/**
 * Created by Phil on 11/11/2016.
 */
public class API
{
    //Constant denoting the size of the header, 16 bytes.
    private int headerSize = 16;

    //A designated constant that determines the maximum amount of time allowable between
    //when a packet is sent and when the corresponding acknowledgement packet can be
    //received. If this value is reached by the timer, the packet will be retransmitted.
    private int rcpTimeout = 1000;

    //A timer object that keeps track of the current amount of time elapsed since the current
    //oldest packet was sent. When the current oldest unacknowledged packet sent is
    //acknowledged, this timer is reset for the new oldest unacknowledged packet. Uses a
    //maximum time elapsed defined by the Timeout variable.
    private ExecutorService executor;

    //The sequence number of the next packet to be sent.
    private int nextSeq = 0;

    //A dynamic value representing the maximum size of the allowable packets to be sent
    //unacknowledged.
    private static int rcpWindow = 1;

    //A dynamic value that is stored within the recipient’s executing host code. This value
    //increments and changes as segments are successfully received, verified, and an
    //acknowledgement is sent back to the sender. expectedSeq functions to keep track of the
    //sequence number that the recipient expects to receive, thereby dealing with out-of-order
    //and duplicate packets.
    private int expectedSeq = 0;

    short incomingSrcPort;
    short incomingDestPort;
    int incomingSeqNum;
    int incomingAckNum;
    short incomingFlags;
    short incomingChecksum;
    byte[] incomingData;

    short outgoingSrcPort;
    short outgoingDestPort;
    int outgoingSeqNum;
    int ougoingAckNum;
    short outgoingFlags;
    short outgoingChecksum;



    private void rcp_send(byte[] segment, InetAddress ip, DatagramSocket socket)
    {


    }


    private byte[] rcp_rec(DatagramSocket socket) throws IOException
    {
        byte[] recData = new byte[1516];

        DatagramPacket recSegment = new DatagramPacket(recData, recData.length);

        socket.receive(recSegment);

        return recData;

    }

    private void seg_unpack(byte[] segment)
    {
        //Unpack source port.
        incomingSrcPort = getSrcPort(segment);

        //unpack destination port.
        incomingDestPort = getDestPort(segment);

        //Unpack sequence number:
        incomingSeqNum = getSeqNum(segment);

        //Unpack acknowledgement number:
        incomingAckNum = getAckNum(segment);

        //Unpack flags:
        incomingFlags = getFlags(segment);

        //Unpack checksum:
        incomingChecksum = getChecksum(segment);

        //Unpack application data:
        incomingData = getData(segment);
    }

    private short getSrcPort(byte[] segment)
    {
        short srcPort = -1;

        ByteBuffer shortBuf = ByteBuffer.allocate(2);
        shortBuf.order(ByteOrder.LITTLE_ENDIAN);

        byte srcPort1 = segment[0];
        byte srcPort2 = segment[1];
        shortBuf.clear();
        shortBuf.put(srcPort1);
        shortBuf.put(srcPort2);
        srcPort = shortBuf.getShort(0);

        return srcPort;
    }

    private short getDestPort(byte[] segment)
    {
        short destPort = -1;

        ByteBuffer shortBuf = ByteBuffer.allocate(2);
        shortBuf.order(ByteOrder.LITTLE_ENDIAN);

        byte destPort1 = segment[2];
        byte destPort2 = segment[3];
        shortBuf.clear();
        shortBuf.put(destPort1);
        shortBuf.put(destPort2);
        destPort = shortBuf.getShort(0);

        return destPort;
    }

    private int getSeqNum(byte[] segment)
    {
        int seqNum = -1;

        ByteBuffer intBuf = ByteBuffer.allocate(4);
        intBuf.order(ByteOrder.LITTLE_ENDIAN);

        byte seq1 = segment[4];
        byte seq2 = segment[5];
        byte seq3 = segment[6];
        byte seq4 = segment[7];
        intBuf.clear();
        intBuf.put(seq1);
        intBuf.put(seq2);
        intBuf.put(seq3);
        intBuf.put(seq4);
        seqNum = intBuf.getInt(0);

        return seqNum;
    }

    private int getAckNum(byte[] segment)
    {
        int ackNum = -1;

        ByteBuffer intBuf = ByteBuffer.allocate(4);
        intBuf.order(ByteOrder.LITTLE_ENDIAN);

        byte ack1 = segment[8];
        byte ack2 = segment[9];
        byte ack3 = segment[10];
        byte ack4 = segment[11];
        intBuf.clear();
        intBuf.put(ack1);
        intBuf.put(ack2);
        intBuf.put(ack3);
        intBuf.put(ack4);
        ackNum = intBuf.getInt(0);

        return ackNum;
    }

    private short getFlags(byte[] segment)
    {
        short flags = -1;

        ByteBuffer shortBuf = ByteBuffer.allocate(2);
        shortBuf.order(ByteOrder.LITTLE_ENDIAN);

        byte flag1 = segment[12];
        byte flag2 = segment[13];
        shortBuf.clear();
        shortBuf.put(flag1);
        shortBuf.put(flag2);
        flags = shortBuf.getShort(0);

        return flags;
    }

    private short getChecksum(byte[] segment)
    {
        short checksum = -1;

        ByteBuffer shortBuf = ByteBuffer.allocate(2);
        shortBuf.order(ByteOrder.LITTLE_ENDIAN);

        byte checksum1 = segment[14];
        byte checksum2 = segment[15];
        shortBuf.clear();
        shortBuf.put(checksum1);
        shortBuf.put(checksum2);
        checksum = shortBuf.getShort(0);

        return checksum;
    }

    private byte[] getData(byte[] segment)
    {
        byte[] data = new byte[segment.length - 16];
        for(int i = 16; i < segment.length; i++)
            data[i] = segment[i];

        return data;
    }

    private short gen_checksum(byte[] segment)
    {
        short sum = 0;

        ByteBuffer shortBuf = ByteBuffer.allocate(2);
        shortBuf.order(ByteOrder.LITTLE_ENDIAN);

        short src = getSrcPort(segment);
        short dest = getDestPort(segment);
        short flags = getFlags(segment);
        short ackNum1;
        short ackNum2;
        short seqNum1;
        short seqNum2;


        byte ack1 = segment[8];
        byte ack2 = segment[9];
        byte ack3 = segment[10];
        byte ack4 = segment[11];

        shortBuf.clear();
        shortBuf.put(ack1);
        shortBuf.put(ack2);
        ackNum1 = shortBuf.getShort(0);

        shortBuf.clear();
        shortBuf.put(ack3);
        shortBuf.put(ack4);
        ackNum2 = shortBuf.getShort(0);

        byte seq1 = segment[4];
        byte seq2 = segment[5];
        byte seq3 = segment[6];
        byte seq4 = segment[7];

        shortBuf.clear();
        shortBuf.put(seq1);
        shortBuf.put(seq2);
        seqNum1 = shortBuf.getShort(0);

        shortBuf.clear();
        shortBuf.put(seq3);
        shortBuf.put(seq4);
        seqNum2 = shortBuf.getShort(0);

        sum += src;
        sum += dest;
        sum += ackNum1;
        sum += ackNum2;
        sum += seqNum1;
        sum += seqNum2;
        sum += flags;

        return sum;
    }

    private short negate(short input)
    {
        return input;
    }



}
