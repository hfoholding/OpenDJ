package org.opends.schema.syntaxes;

import static org.opends.server.schema.SchemaConstants.SYNTAX_TELEPHONE_NAME;
import static org.opends.server.schema.SchemaConstants.SYNTAX_TELEPHONE_OID;
import static org.opends.server.schema.SchemaConstants.SYNTAX_TELEPHONE_DESCRIPTION;
import org.opends.server.types.ByteSequence;
import static org.opends.server.util.StaticUtils.isDigit;
import org.opends.messages.MessageBuilder;
import org.opends.messages.Message;
import static org.opends.messages.SchemaMessages.*;
import static org.opends.messages.SchemaMessages.ERR_ATTR_SYNTAX_TELEPHONE_NO_DIGITS;
import org.opends.schema.SchemaUtils;

/**
 * This class implements the telephone number attribute syntax, which is defined
 * in RFC 2252.  Note that this can have two modes of operation, depending on
 * its configuration.  Most of the time, it will be very lenient when deciding
 * what to accept, and will allow anything but only pay attention to the digits.
 * However, it can also be configured in a "strict" mode, in which case it will
 * only accept values in the E.123 international telephone number format.
 */
public class TelephoneNumberSyntax extends SyntaxImplementation
{
  // Indicates whether this matching rule should operate in strict mode.
  private boolean strictMode;

  public TelephoneNumberSyntax(boolean strict)
  {
    super(SYNTAX_TELEPHONE_OID,
        SYNTAX_TELEPHONE_NAME,
        SYNTAX_TELEPHONE_DESCRIPTION,
        SchemaUtils.RFC4512_ORIGIN);
    this.strictMode = strict;
  }

  public boolean isHumanReadable() {
    return false;
  }



  /**
   * Indicates whether the provided value is acceptable for use in an attribute
   * with this syntax.  If it is not, then the reason may be appended to the
   * provided buffer.
   *
   * @param  value          The value for which to make the determination.
   * @param  invalidReason  The buffer to which the invalid reason should be
   *                        appended.
   *
   * @return  <CODE>true</CODE> if the provided value is acceptable for use with
   *          this syntax, or <CODE>false</CODE> if not.
   */
  public boolean valueIsAcceptable(ByteSequence value,
                                   MessageBuilder invalidReason)
  {
    // No matter what, the value can't be empty or null.
    String valueStr;
    if ((value == null) ||
        ((valueStr = value.toString().trim()).length() == 0))
    {
      invalidReason.append(ERR_ATTR_SYNTAX_TELEPHONE_EMPTY.get());
      return false;
    }

    int length = valueStr.length();


    if (strictMode)
    {
      // If the value does not start with a plus sign, then that's not
      // acceptable.
      if (valueStr.charAt(0) != '+')
      {
        Message message = ERR_ATTR_SYNTAX_TELEPHONE_NO_PLUS.get(valueStr);
        invalidReason.append(message);
        return false;
      }


      // Iterate through the remaining characters in the value.  There must be
      // at least one digit, and it must contain only valid digits and separator
      // characters.
      boolean digitSeen = false;
      for (int i=1; i < length; i++)
      {
        char c = valueStr.charAt(i);
        if (isDigit(c))
        {
          digitSeen = true;
        }
        else if (! isSeparator(c))
        {
          Message message = ERR_ATTR_SYNTAX_TELEPHONE_ILLEGAL_CHAR.get(
                  valueStr, String.valueOf(c), i);
          invalidReason.append(message);
          return false;
        }
      }

      if (! digitSeen)
      {
        Message message = ERR_ATTR_SYNTAX_TELEPHONE_NO_DIGITS.get(valueStr);
        invalidReason.append(message);
        return false;
      }


      // If we've gotten here, then we'll consider it acceptable.
      return true;
    }
    else
    {
      // If we are not in strict mode, then all non-empty values containing at
      // least one digit will be acceptable.
      for (int i=0; i < length; i++)
      {
        if (isDigit(valueStr.charAt(i)))
        {
          return true;
        }
      }

      // If we made it here, then we didn't find any digits.
      Message message = ERR_ATTR_SYNTAX_TELEPHONE_NO_DIGITS.get(valueStr);
      invalidReason.append(message);
      return false;
    }
  }



  /**
   * Indicates whether the provided character is a valid separator for telephone
   * number components when operating in strict mode.
   *
   * @param  c  The character for which to make the determination.
   *
   * @return  <CODE>true</CODE> if the provided character is a valid separator,
   *          or <CODE>false</CODE> if it is not.
   */
  private boolean isSeparator(char c)
  {
    switch (c)
    {
      case ' ':
      case '-':
        return true;
      default:
        return false;
    }
  }
}
