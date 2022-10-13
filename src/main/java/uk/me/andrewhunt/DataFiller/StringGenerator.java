package uk.me.andrewhunt.DataFiller;

import java.util.Random;

public class StringGenerator
{
    final int DEFAULT_LENGTH = 255;
    final String alphaCharset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    final String numCharset = "0123456789";
    final String nonAlphaNumCharset = ",.<>/?;:'@#~[{]}";
    Random r = new Random();

    public String generateString(int length)
   {
        return randomString(alphaCharset, length);
   }
   public String generateString()
   {
       return generateString(DEFAULT_LENGTH);
   }

    public String generateAlphaNumericString(int length)
    {
        return randomString(alphaCharset + numCharset, length);
    }
    public String generateAlphaNumericString()
    {
        return generateAlphaNumericString( DEFAULT_LENGTH);
    }

    public String generateNumericString(int length)
    {
        return randomString( numCharset, length);
    }
    public String generateNumericString()
    {
        return generateNumericString( DEFAULT_LENGTH);
    }

    public String generateAlphaNumOtherString(int length)
    {
        return randomString( alphaCharset + numCharset + nonAlphaNumCharset, length);
    }
    public String generateAlphaNumOtherString()
    {
        return generateAlphaNumOtherString( DEFAULT_LENGTH);
    }



    public String randomString(String input,int length)
    {
        StringBuilder sb = new StringBuilder();
        for (int i =0; i < length ; i++)
        {
            sb.append(input.charAt(r.nextInt(input.length())));
        }
        return sb.toString();
    }


}
