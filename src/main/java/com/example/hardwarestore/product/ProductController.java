package com.example.hardwarestore.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import com.example.hardwarestore.invoice.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final InvoiceService invoiceService;
    private final ProductService productService;

    @GetMapping("/addProduct")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        return "aggregateProduct"; // ✅ Matches your actual HTML file name
    }

    @PostMapping("/newProduct")
    public String createProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public Product getProductById(Long productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editProduct(@PathVariable(name = "id") Long productId) {
        ModelAndView mav = new ModelAndView("editProduct");
        Product product = productService.getProductById(productId);
        mav.addObject("product", product);
        return mav;
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/products";
    }

    @GetMapping
    public String viewProducts(Model model, @Param("keyword") String keyword) {
        List<Product> productList = productService.getAllProducts(keyword);
        model.addAttribute("listProducts", productList);
        model.addAttribute("keyword", keyword);
        return "products";
    }

    @GetMapping("/chooseProducts")
    public String viewChooseProducts(Model model, @Param("keyword") String keyword) {
        List<Product> productList = productService.getAllProducts(keyword);
        model.addAttribute("listProducts", productList);
        model.addAttribute("keyword", keyword);
        return "chooseProducts";
    }

    @PostMapping("/generateInvoice")
    public String generateInvoice(@RequestParam Map<String, String> params, Model model) {
        List<ProductDTO> selectedProducts = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Long productId = Long.parseLong(entry.getKey());
            int quantity = (entry.getValue().isEmpty()) ? 0 : Integer.parseInt(entry.getValue());
            if (quantity != 0) {
                ProductDTO product = productService.getProductDTOById(productId);
                if (product != null && quantity <= product.getStock()) {
                    product.setQuantity(quantity);
                    selectedProducts.add(product);
                }
            }
        }
        Double totalPrice = selectedProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        model.addAttribute("selectedProducts", selectedProducts);
        model.addAttribute("totalPrice", totalPrice);
        return "downloadInvoice";
    }

    @PostMapping("/printInvoice")
    public ModelAndView printInvoice(@RequestParam("productIds") List<Long> productIds,
                                     @RequestParam("quantities") List<Integer> quantities,
                                     Model model) {
        Double totalPrice = 0.0;
        List<InvoiceDetail> details = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);
            Product product = productService.getProductById(productId);

            if (product != null && quantity <= product.getStock() && quantity != null) {
                InvoiceDetail detail = new InvoiceDetail();
                detail.setProductName(product.getName());
                detail.setPrice(product.getPrice());
                detail.setQuantity(quantity);

                totalPrice += product.getPrice() * quantity;
                details.add(detail);

                product.setStock(product.getStock() - quantity);
                productService.saveProduct(product);
            }
        }

        Invoice invoice = new Invoice();
        invoice.setDetails(details);
        invoice.setTotalPrice(totalPrice);
        invoiceService.saveInvoice(invoice);

        return new ModelAndView("redirect:/products/generatePDF/" + invoice.getId());
    }

}



