package sample;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class RectangleWithText
        extends StackPane {
    private Rectangle rectangle;
    private Text text;

    public RectangleWithText(double size, String text) {
        this.setLayoutX(0);
        this.setLayoutY(0);
        this.setPadding(new Insets(0, 0, 0, 0));

        this.rectangle = new Rectangle(0, 0, size, size);
        this.rectangle.setLayoutX(0);
        this.rectangle.setLayoutY(0);
        this.rectangle.setFill(Color.TRANSPARENT);
        this.getChildren().add(this.rectangle);

        this.text = new Text(text);
        this.text.setFont(Font.font("Courier New", size * 0.7));
        this.text.setFill(Color.GREY);
        this.getChildren().add(this.text);
    }

    public void setFontWeight(FontWeight fontWeight) {
        Font font = this.text.getFont();
        this.text.setFont(Font.font(font.toString(), fontWeight, font.getSize()));
    }

    public void setFontSize(double fontSize) {
        Font font = this.text.getFont();
        this.text.setFont(Font.font(font.toString(), fontSize));
    }

    public RectangleWithText() {
        this(10, "0");
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }
}
