package framework.matrix.view;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/** Рисовальщик фона-текстуры.
 * Пропорции текстуры сохраняются.
 */
public class ImageBackground implements BackgroundPainter 
{
	private Image 	  image;     	// текстура
    private Dimension compSize;    	// размеры компонента
    private Rectangle imageArea; 	// выводимая область текстуры
         
    /** Конструктор с текстурой из файла.
     * @param path Полный путь к файлу.
     * @throws IllegalArgumentException путь null
     * @throws NullPointerException файл не содержит текстуры.
     * @throws IOException ошибка в/вв при работе с файлом
     */
    public ImageBackground(String path) throws IOException {
        if (path == null)
            throw new IllegalArgumentException();
        Image i = ImageIO.read(new File(path));
        if (i == null)
            throw new NullPointerException();
        
        this.image = i;
        this.compSize = null;
        this.imageArea = new Rectangle();
    }
    
    /** Возвращает текстуру фона.
     * @return 
     */
	public Image getImage() {
        return image;
	}

    /** Устанавливает текстуру фона.
     * Вызовете {@link MatrixView#repaint()} после изменения текстуры.
     * @param i
     * @throws IllegalArgumentException текстура null
     */
	public void setImage(Image i) {
        if (i == null)
            throw new IllegalArgumentException();
        this.image = i;
	}

    /** {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g2d, Dimension size) {                
        float H_comp = (float)size.height / size.width;
        imageArea.width = image.getWidth(null);
        imageArea.height = Math.round(imageArea.width * H_comp);
        if (imageArea.height > image.getHeight(null)) {
            imageArea.height = image.getHeight(null);
            imageArea.width = Math.round(imageArea.height / H_comp);
        }
        imageArea.x = (image.getWidth(null) - imageArea.width) / 2;
        imageArea.y = (image.getHeight(null) - imageArea.height) / 2;
        g2d.drawImage(
          image, 
          0, 0, size.width, size.height,
          imageArea.x, imageArea.y, imageArea.x + imageArea.width, imageArea.y + imageArea.height,
          null);
        
        this.compSize = size;
    }

    /** {@inheritDoc}
     * @throws IllegalStateException {@link #paint} не вызывался ни разу
     */
    @Override
    public void paintPart(Graphics2D g2d, Rectangle area) {
        if (compSize == null)
            throw new IllegalStateException();
        
        Rectangle partImageArea = new Rectangle(
          Math.round(imageArea.x + (float)area.x / compSize.width * imageArea.width),
          Math.round(imageArea.y + (float)area.y / compSize.height * imageArea.height),
          Math.round((float)area.width / compSize.width * imageArea.width),
          Math.round((float)area.height / compSize.height * imageArea.height));
        g2d.drawImage(
          image, 
          area.x, area.y, area.x + area.width, area.y + area.height,
          partImageArea.x, partImageArea.y, partImageArea.x + partImageArea.width, partImageArea.y + partImageArea.height,
          null);
    }
}
