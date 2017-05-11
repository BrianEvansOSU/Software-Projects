import java.util.Comparator;

import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Program reads words from an input file and displays the words alphabetically
 * in an HTML table followed by the word count.
 *
 * @author Brian Evans
 *
 */
public final class WordCounter {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private WordCounter() {
    }

    /**
     * comparator<String> that sorts in alphabetical order.
     *
     *
     * @param str1
     *            the first string to be compared
     * @param str2
     *            the second string to be compared
     * @return res 1 if str1 is alphabetically before str2, 0 if they are equal
     *         and -1 if str1 is alphabetically after str2
     */
    private static Comparator<String> alphaOrder = new Comparator<String>() {
        @Override
        public int compare(String str1, String str2) {
            //alphabetically compares the two strings
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            if (res == 0) {
                res = str1.compareTo(str2);
            }
            return res;
        }
    };

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */
    public static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            strSet.add(c);
            //adds each character in str to strSet
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures
     *
     *          <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     *          </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        char c = text.charAt(position);
        int i = position;

        if (separators.contains(c)) {
            while (separators.contains(c) && i < text.length()) {
                i++;
                if (i < text.length()) {
                    c = text.charAt(i);
                }
                //if the first character is a separator adds one to i for each
                //following consecutive separator
            }
        } else {
            while (!separators.contains(c) && i < text.length()) {
                i++;
                if (i < text.length()) {
                    c = text.charAt(i);
                    //if the first character isn't a separator, adds one to i for
                    //each following consecutive non-separator
                }
            }
        }
        return text.substring(position, i);
        //returns a substring of text from the beginning position too the
        //location of the last consecutive separator or non-separator
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>Words Counted in loc</title> </head> <body>
     * <h2>Words Counted in loc</h2>
     * <hr />
     * <table border="1">
     * <tr>
     * <th>Words</th>
     * <th>Counts</th>
     * </tr>
     *
     * @param out
     *            the output stream
     * @param loc
     *            the input file location
     * @updates out.content
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(SimpleWriter out, String loc) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Words Counted in " + loc + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Words Counted in " + loc + "</h2>");
        out.println("<hr />");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Words</th>");
        out.println("<th>Counts</th>");
        out.println("<tr>");
        //opening tags printed to the output file for the word count HTML
    }

    /**
     * Outputs table rows for each word in the queue strings. Column one
     * contains the word, column two contains the word count. These are the
     * expected elements generated by this method:
     *
     * <tr>
     * <td>word</td>
     * <td>word count</td>
     * </tr>
     *
     * @param out
     *            the output stream
     * @param words
     *            Queue of words that will be counted and added to the table
     * @updates out.content
     * @ensures out.content = #out.content * [the HTML code for definition
     *          links]
     */
    private static void outputRows(Queue<String> words, SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        Queue<String> temp = new Queue1L<>();
        temp.transferFrom(words);
        //temporary queue of words created for iteration purposes
        while (temp.length() > 0) {
            String word = temp.dequeue();
            words.enqueue(word);
            int wordCount = 1;
            //Dequeues first word in temp queue, sets word count to one
            //Adds word back to words queue
            while (temp.length() > 0 && word.equals(temp.front())) {
                word = temp.dequeue();
                words.enqueue(word);
                wordCount++;
                //for String word, checks the first string in temp.  If strings
                //are equal, dequeues string from temp, enqueues it to words,
                //and adds one to word count.
            }
            out.println("<tr>");
            out.println("<td>" + word + "</td>");
            out.println("<td>" + wordCount + "</td>");
            out.println("</tr>");
            //HTML code for each row displaying each word and word count
        }
    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * </table>
     * </body> </html>
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
        //outputs the closing tags for the word count table
    }

    /**
     * Generates a queue containing all the words in an input file sorted
     * alphabetically.
     *
     * @param input
     *            input file to read from
     * @return words = each word in input sorted alphabetically
     */
    public static Queue<String> generateQueue(SimpleReader input) {
        assert input != null : "Violation of: input is not null";

        String separatorStr = " \t\n\r,-.!?[]';:/()";
        //string of separator characters
        Set<Character> separatorSet = new Set1L<>();
        generateElements(separatorStr, separatorSet);
        //creates set of characters from separatorStr

        Queue<String> words = new Queue1L<String>();
        while (!input.atEOS()) {
            String line = input.nextLine();
            int position = 0;
            //while loop to iterate through each line of the input file
            //adds each word that does not contain a separator to the queue words

            while (position < line.length()) {
                String word = nextWordOrSeparator(line, position, separatorSet);
                position += word.length();
                //Loop iterates through each word/separator in the line

                if (!separatorSet.contains(word.charAt(0))) {
                    words.enqueue(word);
                    //adds String word to the queue words if it does not contain
                    //a separator
                }
            }
        }

        words.sort(alphaOrder);
        return words;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Open input and output streams
         */
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.println("Please enter the name of a valid input file ");
        String inFile = in.nextLine();
        SimpleReader input = new SimpleReader1L(inFile);
        out.println("Please enter the name of a valid output file ");
        SimpleWriter output = new SimpleWriter1L(in.nextLine());
        //creates input and output files to read from and print to

        outputHeader(output, inFile);
        //prints the HTML headers to output

        Queue<String> words = generateQueue(input);
        //creates alphabetically sorted queue from the values of map
        outputRows(words, output);
        //prints the terms with links to their definitions
        outputFooter(output);
        //prints the closing HTML tags to output

        //Close input and output streams
        in.close();
        out.close();
        input.close();
        output.close();
    }
}
