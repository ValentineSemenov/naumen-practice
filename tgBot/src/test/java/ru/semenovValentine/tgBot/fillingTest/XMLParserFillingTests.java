package ru.semenovValentine.tgBot.fillingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.semenovValentine.tgBot.entity.Category;
import ru.semenovValentine.tgBot.entity.Product;
import ru.semenovValentine.tgBot.repository.CategoryRepository;
import ru.semenovValentine.tgBot.repository.ProductRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@SpringBootTest
public class XMLParserFillingTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void fillingTest() {
        try {
            ClassPathResource resource = new ClassPathResource("testData.xml");
            File file = resource.getFile();

            Document document = parseXML(file);
            document.getDocumentElement().normalize();

            NodeList categoryList = document.getElementsByTagName("category");
            for (int i = 0; i < categoryList.getLength(); i++) {
                Node categoryNode = categoryList.item(i);
                if (categoryNode.getNodeType() == Node.ELEMENT_NODE) {
                    processCategory((Element) categoryNode);
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Ошибка в обработке XML файла", e);
        }
    }

    private Document parseXML(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    private void processCategory(Element categoryElement) {
        String categoryName = categoryElement.getAttribute("name");
        Category baseCategory = new Category(categoryName, null);
        categoryRepository.save(baseCategory);

        NodeList subcategoryList = categoryElement.getElementsByTagName("subcategory");
        for (int j = 0; j < subcategoryList.getLength(); j++) {
            Node subcategoryNode = subcategoryList.item(j);
            if (subcategoryNode.getNodeType() == Node.ELEMENT_NODE) {
                processSubcategory((Element) subcategoryNode, baseCategory);
            }
        }
    }

    private void processSubcategory(Element subcategoryElement, Category baseCategory) {
        String subcategoryName = subcategoryElement.getAttribute("name");
        Category subcategory = new Category(subcategoryName, baseCategory);
        categoryRepository.save(subcategory);

        NodeList productList = subcategoryElement.getElementsByTagName("product");
        for (int k = 0; k < productList.getLength(); k++) {
            Node productNode = productList.item(k);
            if (productNode.getNodeType() == Node.ELEMENT_NODE) {
                processProduct((Element) productNode, subcategory);
            }
        }
    }

    private void processProduct(Element productElement, Category subcategory) {
        String productName = productElement.getAttribute("name");
        String productDescription = productElement.getAttribute("description");
        Double productPrice = Double.parseDouble(productElement.getAttribute("price"));

        Product product = new Product(subcategory, productName, productDescription, productPrice);
        productRepository.save(product);
    }
}
