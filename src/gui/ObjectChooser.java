

package gui;

import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 *
 * @author Tug
 */
public class ObjectChooser extends List
{
    private Vector objects;

    public ObjectChooser()
    {
        this(new String[0], null);
    }

    public ObjectChooser(String[] stringElements, Image[] imageElements)
    {
        super("", Choice.IMPLICIT, stringElements, imageElements);
        this.objects = new Vector();
    }

    public Object getSelectedObject()
    {
        return objects.elementAt(getSelectedIndex());
    }

    public String getSelectedString()
    {
        return getString(getSelectedIndex());
    }

    public Image getSelectedImage()
    {
        return getImage(getSelectedIndex());
    }

    public void append(String stringPart, Object objectPart, Image imagePart)
    {
        super.append(stringPart, imagePart);
        objects.addElement(objectPart);
    }

    public void append(String stringPart, Object objectPart)
    {
        this.append(stringPart, objectPart, null);
    }

    public void delete(int elementNum)
    {
        super.delete(elementNum);
        objects.removeElementAt(elementNum);
    }

    public void insert(int elementNum, String stringPart, Object objectPart, Image imagePart)
    {
        super.insert(elementNum, stringPart, imagePart);
        objects.insertElementAt(objectPart, elementNum);
    }

}
