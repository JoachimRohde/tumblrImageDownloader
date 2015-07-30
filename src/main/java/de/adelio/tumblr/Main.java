/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Joachim F. Rohde
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package de.adelio.tumblr;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * http://spongebob-closeups.tumblr.com/
 *
 * @author jr
 */
public class Main
{

    public static ArrayList<String> siteNames = new ArrayList();
    public static String DEST_PATH = System.getProperty("user.home") + "/tumblrImages/";
    public static boolean updatePrevious = false;
    public static boolean stopAfterFirstHit = true;

    public static void main(String[] args)
    {

        if (args.length == 0)
        {
            showHelp();
        } else
        {
            for (int i = 0; i < args.length; i++)
            {

                // todo: parameter: start with page xx
                if (args[i].startsWith("-"))
                {
                    // argument is an option
                    if (args[i].trim().toLowerCase().startsWith("-p"))
                    {
                        // specify download directory
                        DEST_PATH = args[i].trim().substring("-p".length());
                        if (!DEST_PATH.endsWith("/"))
                        {
                            DEST_PATH = DEST_PATH + "/";
                        }
                    }
                    if (args[i].trim().equalsIgnoreCase("-u"))
                    {
                        // update previous downloaded sites
                        updatePrevious = true;
                    }
                    if (args[i].trim().equalsIgnoreCase("-ds"))
                    {
                        // usually we stop searching for further images
                        // if we find one which is already downloaded
                        // but with this flag we can continue the search, in case
                        // in a previous run not all images have been downloaded
                        stopAfterFirstHit = false;
                    }
                } else
                {
                    // argument is a site
                    if (args[i].contains("tumblr.com"))
                    {
                        if (args[i].startsWith("http"))
                        {
                            args[i] = args[i].substring(args[i].indexOf("://") + "://".length());
                            args[i] = args[i].substring(0, args[i].indexOf(".tumblr.com"));
                        } else
                        {
                            args[i] = args[i].substring(0, args[i].indexOf(".tumblr.com"));
                        }
                    }

                    siteNames.add(args[i]);
                }
            }

            checkDestinationDirectory();
            if (updatePrevious)
            {
                readPrevious();
            }

            downloadImages();
        }
    }

    /**
     * Checks if the destination directory exists and creates it, if necessary.
     */
    public static void checkDestinationDirectory()
    {
        checkDestinationDirectory(DEST_PATH);
    }

    /**
     * Checks if the destination directory exists and creates it, if necessary.
     */
    public static void checkDestinationDirectory(String pathToCheck)
    {
        File destDir = new File(pathToCheck);
        if (!destDir.exists())
        {
            if (!destDir.mkdirs())
            {
                System.out.println("Error creating destination directory '" + pathToCheck + "'. We will exit.");
                System.exit(1);
            }
        }
    }

    public static void readPrevious()
    {

        File[] directories = new File(DEST_PATH).listFiles(File::isDirectory);
        for (File f : directories)
        {
            siteNames.add(f.getName());
        }

    }

    public static void downloadImages()
    {

        URL url;
        InputStream is = null;
        BufferedReader br, postBr;
        String line, postLine;

        try
        {

            for (String sitename : siteNames)
            {
                // change this variable if you don't want to start on page 1
                int currentPage = 1;
                boolean foundImage = true;
                boolean foundPost = false;
                int postCounter = 0;
                int imageCounter = 0;
                boolean continueSearching = true;

                checkDestinationDirectory(DEST_PATH + sitename);

                while (foundImage && continueSearching)
                {

                    System.out.println("------------------ Page " + currentPage + " ------------------");

                    url = new URL("http://" + sitename + ".tumblr.com/page/" + currentPage);
                    is = url.openStream();
                    br = new BufferedReader(new InputStreamReader(is));

                    foundPost = false;

                    // read index-page
                    while ((line = br.readLine()) != null && continueSearching)
                    {

                        if (line.contains("<a") && line.contains(".tumblr.com/post"))
                        {
                            foundPost = true;
                            postCounter++;

                            String postUrl = line.substring(line.indexOf("href=") + "href=".length() + 1, line.length());
                            try
                            {
                                postUrl = postUrl.substring(0, postUrl.indexOf("\""));
                            } catch (Exception e)
                            {
                                continue;
                            }

                            System.out.println(postUrl);

                            try
                            {
                                url = new URL(postUrl);
                                is = url.openStream();
                            } catch (Exception e)
                            {
                                continue;
                            }
                            postBr = new BufferedReader(new InputStreamReader(is));

                            // read post-page
                            while ((postLine = postBr.readLine()) != null && continueSearching)
                            {
                                if (postLine.contains("<img") && postLine.contains("media.tumblr") && !postLine.contains("/avatar"))
                                {
                                    String imgUrl = postLine.substring(postLine.indexOf("<img") + "<img".length(), postLine.length());
                                    try
                                    {
                                        imgUrl = imgUrl.substring(imgUrl.indexOf("src=") + "src=".length() + 1, imgUrl.length());
                                        imgUrl = imgUrl.substring(0, imgUrl.indexOf("\""));

                                        File fileToSave = new File(DEST_PATH + sitename + File.separatorChar + imgUrl.substring(imgUrl.lastIndexOf("/") + 1));
                                        if (fileToSave.exists())
                                        {
                                            System.out.println("File exists already");

                                            // file exists... check if we should continue searching
                                            if (stopAfterFirstHit)
                                            {
                                                continueSearching = false;
                                                System.out.println("We stop the search here, since we downloaded this image already in the past.");
                                            }

                                        } else
                                        {
                                            BufferedImage image = null;

                                            image = ImageIO.read(new URL(imgUrl));
                                            ImageIO.write(image, imgUrl.substring(imgUrl.lastIndexOf(".") + 1), fileToSave);
                                            imageCounter++;
                                        }
                                    } catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    } catch (Exception e)
                                    {
                                        continue;
                                    }

                                    System.out.println("imageUrl " + imgUrl);

                                }

                            }

                        }
                    }

                    if (!foundPost)
                    {
                        foundImage = false;
                    } else
                    {
                        currentPage++;
                    }

                }
                System.out.println("Posts (" + sitename + "):" + postCounter);
                System.out.println("Images(" + sitename + "):" + imageCounter);
                postCounter = 0;
                imageCounter = 0;
            }

        } catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

    }

    public static void showHelp()
    {
        System.out.println("Usage: java -jar tumblrImageDownloader [sitename...] [options...]");
        System.out.println("Example usage: java -jar tumblrImageDownloader http://spongebob-closeups.tumblr.com/ -p/tmp");
        System.out.println("Example usage: java -jar tumblrImageDownloader spongebob-closeups.tumblr.com -u");
        System.out.println("Example usage: java -jar tumblrImageDownloader spongebob-closeups -u -ds");
        System.out.println("Example usage: java -jar tumblrImageDownloader -u");
        System.out.println("If a dowload directory is not specified, images will be saved in: [home-directory]/tumblrImages/");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("-u              Check for updates. This option will search for subdirectories in the destination directory and use the subdirectory-name as the name of the tumblr-page.");
        System.out.println("-p[path]        Specify download directory ");
        System.out.println("-ds             don't stop downloading a site after the first image was found which has been already downloaded ");
    }
}
