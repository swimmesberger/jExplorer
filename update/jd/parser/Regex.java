/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 *
 * This file is part of org.appwork.utils
 *
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package jd.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jd.utils.StringUtil;

/**
 * @author thomas
 */
public class Regex
{

    private Matcher matcher;

    public Regex(final Matcher matcher)
    {
        if (matcher != null)
        {
            this.matcher = matcher;
        }
    }

    public Regex(final Object data, final Pattern pattern)
    {
        this(data.toString(), pattern);
    }

    public Regex(final Object data, final String pattern)
    {
        this(data.toString(), pattern);
    }

    public Regex(final Object data, final String pattern, final int flags)
    {
        this(data.toString(), pattern, flags);
    }

    public Regex(final String data, final Pattern pattern)
    {
        if ((data != null) && (pattern != null))
        {
            this.matcher = pattern.matcher(data);
        }
    }

    public Regex(final String data, final String pattern)
    {
        if ((data != null) && (pattern != null))
        {
            this.matcher = Pattern.compile(pattern, 34).matcher(data);
        }
    }

    public Regex(final String data, final String pattern, final int flags)
    {
        if ((data != null) && (pattern != null))
        {
            this.matcher = Pattern.compile(pattern, flags).matcher(data);
        }
    }

    public int count()
    {
        if (this.matcher == null)
        {
            return 0;
        }
        this.matcher.reset();
        int c = 0;
        Matcher matchertmp = this.matcher;
        while (matchertmp.find())
        {
            c++;
        }
        return c;
    }

    public String getMatch(int group)
    {
        if (this.matcher != null)
        {
            Matcher matcher = this.matcher;
            matcher.reset();
            if (matcher.find())
            {
                return matcher.group(group + 1);
            }
        }
        return null;
    }

    public Matcher getMatcher()
    {
        if (this.matcher != null)
        {
            this.matcher.reset();
        }
        return this.matcher;
    }

    public String[][] getMatches()
    {
        if (this.matcher == null)
        {
            return null;
        }
        Matcher matcher = this.matcher;
        matcher.reset();
        ArrayList ar = new ArrayList();
        while (matcher.find())
        {
            int c = matcher.groupCount();
            int d = 1;
            String[] group;
            if (c == 0)
            {
                group = new String[c + 1];
                d = 0;
            }
            else
            {
                group = new String[c];
            }

            for (int i = d; i <= c; i++)
            {
                group[(i - d)] = matcher.group(i);
            }
            ar.add(group);
        }
        return ar.size() == 0 ? new String[0][] : (String[][]) ar.toArray(new String[0][]);
    }

    public String[] getColumn(int x)
    {
        if (this.matcher == null)
        {
            return null;
        }
        x++;
        Matcher matcher = this.matcher;
        matcher.reset();

        ArrayList ar = new ArrayList();
        while (matcher.find())
        {
            ar.add(matcher.group(x));
        }
        return (String[]) ar.toArray(new String[ar.size()]);
    }

    public boolean matches()
    {
        Matcher matcher = this.matcher;
        if (matcher == null)
        {
            return false;
        }
        matcher.reset();
        return matcher.find();
    }

    public void setMatcher(Matcher matcher)
    {
        this.matcher = matcher;
    }

    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        String[][] matches = getMatches();
        int matchesLength = matches.length;

        for (int i = 0; i < matchesLength; i++)
        {
            String[] match = matches[i];
            int matchLength = match.length;
            for (int j = 0; j < matchLength; j++)
            {
                ret.append("match[");
                ret.append(i);
                ret.append("][");
                ret.append(j);
                ret.append("] = ");
                ret.append(match[j]);

                ret.append(StringUtil.LINE_SEPARATOR);
            }
        }
        this.matcher.reset();
        return ret.toString();
    }

    public static long getMilliSeconds(String wait)
    {
        String[][] matches = new Regex(wait, "([\\d]+) ?[\\.|\\,|\\:] ?([\\d]+)").getMatches();
        if ((matches == null) || (matches.length == 0))
        {
            matches = new Regex(wait, Pattern.compile("([\\d]+)")).getMatches();
        }

        if ((matches == null) || (matches.length == 0))
        {
            return -1L;
        }

        double res = 0.0D;
        if (matches[0].length == 1)
        {
            res = Double.parseDouble(matches[0][0]);
        }
        if (matches[0].length == 2)
        {
            res = Double.parseDouble(matches[0][0] + "." + matches[0][1]);
        }

        if (matches(wait, Pattern.compile("(h|st)", 2)))
        {
            res *= 3600000.0D;
        }
        else if (matches(wait, Pattern.compile("(m)", 2)))
        {
            res *= 60000.0D;
        }
        else
        {
            res *= 1000.0D;
        }
        return Math.round(res);
    }

    public static long getMilliSeconds(String expire, String timeformat, Locale l)
    {
        if (expire != null)
        {
            SimpleDateFormat dateFormat = l != null ? new SimpleDateFormat(timeformat, l) : new SimpleDateFormat(timeformat);
            try
            {
                return dateFormat.parse(expire).getTime();
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        return -1L;
    }

    public String getMatch(int entry, int group)
    {
        if (this.matcher != null)
        {
            Matcher matcher = this.matcher;
            matcher.reset();

            entry++;
            int groupCount = 0;
            while (matcher.find())
            {
                if (groupCount == group)
                {
                    return matcher.group(entry);
                }
                groupCount++;
            }
        }
        return null;
    }

    public String[] getRow(int y)
    {
        if (this.matcher != null)
        {
            Matcher matcher = this.matcher;
            matcher.reset();
            int groupCount = 0;
            while (matcher.find())
            {
                if (groupCount == y)
                {
                    int c = matcher.groupCount();

                    String[] group = new String[c];

                    for (int i = 1; i <= c; i++)
                    {
                        group[(i - 1)] = matcher.group(i);
                    }
                    return group;
                }
                groupCount++;
            }
        }
        return null;
    }

    public static int getMilliSeconds2(String wait)
    {
        String minutes = new Regex(wait, "(\\d*?)[ ]*m").getMatch(0);
        String hours = new Regex(wait, "(\\d*?)[ ]*(h|st)").getMatch(0);
        String seconds = new Regex(wait, "(\\d*?)[ ]*se").getMatch(0);
        if (minutes == null)
        {
            minutes = "0";
        }
        if (hours == null)
        {
            hours = "0";
        }
        if (seconds == null)
        {
            seconds = "0";
        }
        return Integer.parseInt(hours) * 60 * 60 * 1000 + Integer.parseInt(minutes) * 60 * 1000 + Integer.parseInt(seconds) * 1000;
    }

    public static String escape(String pattern)
    {
        char[] specials =
        {
            '(', '[', '{', '\\', '^', '-', '$', '|', ']', '}', ')', '?', '*', '+', '.'
        };
        int patternLength = pattern.length();
        StringBuilder sb = new StringBuilder();
        sb.setLength(patternLength);

        for (int i = 0; i < patternLength; i++)
        {
            char act = pattern.charAt(i);
            for (char s : specials)
            {
                if (act == s)
                {
                    sb.append('\\');
                    break;
                }
            }
            sb.append(act);
        }
        return sb.toString().trim();
    }

    public static String[] getLines(String arg)
    {
        if (arg == null)
        {
            return new String[0];
        }
        String[] temp = arg.split("[\r\n]{1,2}");
        int tempLength = temp.length;
        String[] output = new String[tempLength];
        for (int i = 0; i < tempLength; i++)
        {
            output[i] = temp[i].trim();
        }
        return output;
    }

    public static long getSize(String string)
    {
        String[][] matches = new Regex(string, Pattern.compile("([\\d]+)[\\.|\\,|\\:]([\\d]+)", 2)).getMatches();

        if ((matches == null) || (matches.length == 0))
        {
            matches = new Regex(string, Pattern.compile("([\\d]+)", 2)).getMatches();
        }
        if ((matches == null) || (matches.length == 0))
        {
            return -1L;
        }

        double res = 0.0D;
        if (matches[0].length == 1)
        {
            res = Double.parseDouble(matches[0][0]);
        }
        if (matches[0].length == 2)
        {
            res = Double.parseDouble(matches[0][0] + "." + matches[0][1]);
        }
        if (matches(string, Pattern.compile("(gb|gbyte|gig)", 2)))
        {
            res *= 1073741824.0D;
        }
        else if (matches(string, Pattern.compile("(mb|mbyte|megabyte)", 2)))
        {
            res *= 1048576.0D;
        }
        else if (matches(string, Pattern.compile("(kb|kbyte|kilobyte)", 2)))
        {
            res *= 1024.0D;
        }

        return Math.round(res);
    }

    public static boolean matches(Object str, Pattern pat)
    {
        return new Regex(str, pat).matches();
    }

    public static boolean matches(Object page, String string)
    {
        return new Regex(page, string).matches();
    }
}
