/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 */


package org.opends.server.backends.index;

import java.util.*;
import org.opends.server.types.DirectoryException;
import org.opends.server.api.ApproximateMatchingRule;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeValue;
import static org.opends.server.util.ServerConstants.*;


/**
 * This class provides an implementation for generating index keys
 * for an approximate matching rule. This default implementation should
 * work for most of the approximate matching rules.
 */
public final class ApproximateIndexKeyFactory extends IndexKeyFactory
{
  //The approximate  matching rule to be used for indexing.
  private ApproximateMatchingRule matchingRule;


  
  //The byte-by-byte comparator for comparing the keys.
  private Comparator<byte[]> comparator;



  /**
  * Create a new approximate index key factory for the given equality
  * matching rule.
  *
  * @param matchingRule The approximate matching rule which will be used
  *                                    to create keys.
  */
  public ApproximateIndexKeyFactory(ApproximateMatchingRule matchingRule)
  {
    this.matchingRule = matchingRule;
    comparator = new DefaultByteKeyComparator();
  }



  /**
  * {@inheritDoc}
  */
  @Override
  public String getIndexID()
  {
    return APPROXIMATE_INDEX_ID;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void getKeys(List<Attribute> attrList, KeySet keySet)
  {
    for (Attribute attr : attrList)
    {
      for(AttributeValue value : attr)
      {
        try
        {
          keySet.addKey(matchingRule.normalizeValue(value.getValue()));
        }
        catch (DirectoryException de)
        {
        }
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Comparator<byte[]> getComparator()
  {
    return comparator;
  }
}
