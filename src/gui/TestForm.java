
package gui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Spacer;
import javax.wireless.messaging.TextMessage;

/**
 *
 * @author Theodore
 */

// Forme d'affichage pour tester la reception d'un sms.
public class TestForm extends Form
{
    private String alertMessage;
    private String content;

    public TestForm() {
        super("");
        this.alertMessage = "Contenu :\n";
        this.content = "[vide]";
        append(alertMessage);
        append(content);
    }

    public TestForm(TextMessage content) {
        super("");
        this.alertMessage = "Contenu :\n";
        this.content = content.getPayloadText();
        append(this.alertMessage);
        append(this.content);
    }

    public TestForm(String content) {
        super("");
        this.alertMessage = "Contenu :\n";
        this.content = content;
        append(this.alertMessage);
        append(this.content);
    }

}
