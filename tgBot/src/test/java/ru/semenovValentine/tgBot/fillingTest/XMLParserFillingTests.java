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
import ru.semenovValentine.tgBot.entity.Client;
import ru.semenovValentine.tgBot.entity.ClientOrder;
import ru.semenovValentine.tgBot.entity.OrderProduct;
import ru.semenovValentine.tgBot.entity.Product;
import ru.semenovValentine.tgBot.dao.CategoryRepository;
import ru.semenovValentine.tgBot.dao.ClientOrderRepository;
import ru.semenovValentine.tgBot.dao.ClientRepository;
import ru.semenovValentine.tgBot.dao.OrderProductRepository;
import ru.semenovValentine.tgBot.dao.ProductRepository;

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

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

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
            NodeList clientList = document.getElementsByTagName("client");
            for (int i = 0; i < clientList.getLength(); i++) {
                Node clientNode = clientList.item(i);
                if (clientNode.getNodeType() == Node.ELEMENT_NODE) {
                    processClient((Element) clientNode);
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Ошибка в обработке XML файла", e);
        }
    }

    private void processClient(Element clientElement) {
        Long externalId = Long.parseLong(clientElement.getAttribute("externalId"));
        String fullName = clientElement.getAttribute("fullName");
        String phoneNumber = clientElement.getAttribute("phoneNumber");
        String address = clientElement.getAttribute("address");

        Client client = new Client(externalId, fullName, phoneNumber, address);
        clientRepository.save(client);

        NodeList orderList = clientElement.getElementsByTagName("order");
        for (int j = 0; j < orderList.getLength(); j++) {
            Node orderNode = orderList.item(j);
            if (orderNode.getNodeType() == Node.ELEMENT_NODE) {
                processClientOrder((Element) orderNode, client);
            }
        }
    }

    private void processClientOrder(Element orderElement, Client client) {
        int status = Integer.parseInt(orderElement.getAttribute("status"));
        double total = Double.parseDouble(orderElement.getAttribute("total"));

        ClientOrder clientOrder = new ClientOrder(client, status, total);
        clientOrderRepository.save(clientOrder);

        NodeList orderProductList = orderElement.getElementsByTagName("orderProduct");
        for (int k = 0; k < orderProductList.getLength(); k++) {
            Node orderProductNode = orderProductList.item(k);
            if (orderProductNode.getNodeType() == Node.ELEMENT_NODE) {
                processOrderProduct((Element) orderProductNode, clientOrder);
            }
        }
    }

    private void processOrderProduct(Element orderProductElement, ClientOrder clientOrder) {
        Long productId = Long.parseLong(orderProductElement.getAttribute("productId"));
        int count = Integer.parseInt(orderProductElement.getAttribute("count"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + productId + " не найден"));

        OrderProduct orderProduct = new OrderProduct(clientOrder, product, count);
        orderProductRepository.save(orderProduct);
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
