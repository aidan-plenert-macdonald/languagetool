/* LanguageTool, a natural language style checker
 * Copyright (C) 2019 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.nl;

import org.apache.commons.lang3.StringUtils;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.patterns.RuleFilter;

import java.util.Map;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

public class CompoundFilter extends RuleFilter {

  @Override
  public RuleMatch acceptRuleMatch(RuleMatch match, Map<String, String> arguments, int patternTokenPos, AnalyzedTokenReadings[] patternTokens) {
    String words = "";
    for (int i = 1; i < 6; i++) {
      String arg = arguments.get("word"+i);
      if (arg != null) {
        words = words + " " + arguments.get("word" + i);
      }
    }
    words = words.substring(1);
    String repl = glueParts(words);
    String message = match.getMessage().replaceAll("<suggestion>.*?</suggestion>", "<suggestion>" + repl + "</suggestion>");
    String shortMessage = match.getShortMessage().replaceAll("<suggestion>.*?</suggestion>", "<suggestion>" + repl + "</suggestion>");
    RuleMatch newMatch = new RuleMatch(match.getRule(), match.getSentence(), match.getFromPos(), match.getToPos(), message, shortMessage);
    newMatch.setSuggestedReplacement(repl);
    return newMatch;
  }

  private static String glueParts(String s) {
    String spelledWords = "(abc|adv|aed|apk|b2b|bh|bhv|bso|btw|bv|cao|cd|cfk|ckv|cv|dc|dj|dtp|dvd|fte|gft|ggo|ggz|gm|gmo|gps|gsm|hbo|" +
      "hd|hiv|hr|hrm|hst|ic|ivf|kmo|lcd|lp|lpg|lsd|mbo|mdf|mkb|mms|msn|mt|ngo|nv|ob|ov|ozb|p2p|pc|pcb|pdf|pk|pps|" +
      "pr|pvc|roc|rvs|sms|tbc|tbs|tl|tv|uv|vbo|vj|vmbo|vsbo|vwo|wc|wo|xtc|zzp)";
    String[] parts = s.split(" ");
    String compound = parts[0];
    for (int i = 1; i < parts.length; i++) {
      String word2 = parts[i];
      char lastChar = compound.charAt(compound.length() - 1);
      char firstChar = word2.charAt(0);
      String connection = lastChar + String.valueOf(firstChar);
      if (StringUtils.containsAny(connection, "aa", "ae", "ai", "ao", "au", "ee", "ei", "eu", "ie", "ii", "oe", "oi", "oo", "ou", "ui", "uu", "ij")) {
        compound = compound + '-' + word2;
      } else if (isUpperCase(firstChar) && isLowerCase(lastChar)) {
        compound = compound + '-' + word2;
      } else if (isUpperCase(lastChar) && isLowerCase(firstChar)) {
        compound = compound + '-' + word2;
      } else if (compound.matches("(^|.+-)?" + spelledWords) || word2.matches(spelledWords + "(-.+|$)?")) {
        compound = compound + '-' + word2;
      } else if (compound.matches(".+-[a-z]$") || word2.matches("^[a-z]-.+")) {
        compound = compound + '-' + word2;
      } else {
        compound = compound + word2;
      }
    }
    return compound;
  }

}
