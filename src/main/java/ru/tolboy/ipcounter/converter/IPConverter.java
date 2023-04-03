package ru.tolboy.ipcounter.converter;

import java.util.function.ToIntFunction;

/**
 * The converter to transform an IP address as CharSequence to an integer data type.
 */
public class IPConverter implements ToIntFunction<CharSequence> {
    /**
     * This implementation processes the CharSequence character by character, parsing each octet as it is encountered.
     * It maintains a running count of the number of octets seen so far, and shifts and adds each octet to the integer
     * representation of the IP address as it is parsed. The implementation also includes error checking to ensure that
     * the input CharSequence represents a valid IPv4 address with four octets, each in the range from 0 to 255.
     *
     * @param ipAddress - IPs char sequence
     * @return IP as integer
     */
    @Override
    public int applyAsInt(CharSequence ipAddress) {
        int octetCount = 0;
        int octet = 0;
        int ipAsInt = 0;

        for (int i = 0; i < ipAddress.length(); i++) {
            char c = ipAddress.charAt(i);
            if (c == '.') {
                ipAsInt = (ipAsInt << 8) + octet;
                octet = 0;
                octetCount++;
            } else if (c >= '0' && c <= '9') {
                octet = (octet * 10) + (c - '0');
            } else {
                throw new IllegalArgumentException("Invalid IP address: " + ipAddress);
            }
        }

        if (octetCount != 3 || octet < 0 || octet > 255) {
            throw new IllegalArgumentException("Invalid IP address: " + ipAddress);
        }

        ipAsInt = (ipAsInt << 8) + octet;
        return ipAsInt;
    }

}
