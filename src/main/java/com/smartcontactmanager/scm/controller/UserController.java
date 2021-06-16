package com.smartcontactmanager.scm.controller;

import com.razorpay.*;
import com.smartcontactmanager.scm.dao.ContactRepository;
import com.smartcontactmanager.scm.dao.MyOrderRepository;
import com.smartcontactmanager.scm.dao.UserRepository;
import com.smartcontactmanager.scm.entities.Contact;
import com.smartcontactmanager.scm.entities.MyOrder;
import com.smartcontactmanager.scm.entities.User;
import com.smartcontactmanager.scm.helper.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private MyOrderRepository myOrderRepository;

    //method for adding data to response
    @ModelAttribute
    public void addCommonData(Model m, Principal principal){

        String username = principal.getName();
        System.out.println("USERNAME "+username);

        User user = userRepository.getUserByUserName(username);
        System.out.println("USER "+user);
        m.addAttribute("user",user);
    }

    //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){

//        String username = principal.getName();
//        System.out.println("USERNAME "+username);
//
//        User user = userRepository.getUserByUserName(username);
//        System.out.println("USER "+user);
//        model.addAttribute("user",user);
        model.addAttribute("title", "User Dashboard");

//        get the user using username(Email)
        return "normal/user_dashboard";
    }

    //Open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }
//    processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage")MultipartFile file,
                                 Principal principal, HttpSession session){

        try {


            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

//            if(3>2){
//                    throw new Exception();
//            }

//            processing and uploading file
            if(file.isEmpty()){
//                if file is empty
                System.out.println("File is empty");
                contact.setImage("contact.png");
            }else {
//
//                upload file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");
            }

            contact.setUser(user);


            user.getContacts().add(contact);

            this.userRepository.save(user);

            System.out.println("DATA " + contact);
            System.out.println("Added to database");

//            message success....
            session.setAttribute("Message", new Message("Your contact is added !! Add more", "success"));

        }catch (Exception e){
            System.out.println("ERROR "+e.getMessage());
            e.printStackTrace();
//            Error message
            session.setAttribute("Message", new Message("Something went wrong !! Try again", "danger"));

        }
        return "normal/add_contact_form";
    }

//    Show/View contact handler
//    Per page = 3[n]
//    current page = 0 [page]
    @GetMapping("/show-contact/{page}")
    public String showContacts(@PathVariable("page")Integer page, Model m, Principal principal){
        m.addAttribute("title", "Show User Contacts");
        //        Send contacts to list
        //
        //        String userName = principal.getName();
        //
//        User user = this.userRepository.getUserByUserName(userName);
//        List<Contact> contacts = user.getContacts();

        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        Pageable pageable  = PageRequest.of(page, 3);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contact";
    }

    //Showing particular contact details.
    @RequestMapping("/{cid}/contact")
    public String showContactDetail(@PathVariable("cid")Integer cid, Model m, Principal principal){

        System.out.println("CID "+cid);
        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();

//        get
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId()==contact.getUser().getId()){


            m.addAttribute("contact", contact);
            m.addAttribute("title", contact.getName());

        }

        return "normal/contact_detail";
    }

    //Delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid")Integer cid, Model model,  Principal principal,HttpSession session){

        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        //Check

//        String userName = principal.getName();
//        User user = this.userRepository.getUserByUserName(userName);
//        if(user.getId()==contact.getUser().getId()){
//
//
//            model.addAttribute("contact", contact);
//            model.addAttribute("title", contact.getName());
//
//        }

        System.out.println("Contact"+contact.getCid());
//        contact.setUser(null);

        User user = this.userRepository.getUserByUserName(principal.getName());
        user.getContacts().remove(contact);
        this.userRepository.save(user);


//        this.contactRepository.delete(contact);
        System.out.println("DELETED");

        session.setAttribute("message", new Message("Contact deleted successfully", "success"));
        return "redirect:/user/show-contact/0";
    }

    //Update Form handler
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid")Integer cid, Model m){

        m.addAttribute("title", "Update Contact");

        Contact contact = this.contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);
        return "normal/update_form";
    }

    //Update contact handler
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal){

        try{

//            Old contact details
            Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
            //image

            if(!file.isEmpty()){

                //file work
                //rewrite

                //Delete old photo &
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldContactDetail.getName());
                file1.delete();


                // update new photo

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

                System.out.println("Image is uploaded");


            }else {
                contact.setImage(oldContactDetail.getImage());
            }
            User user =this.userRepository.getUserByUserName(principal.getName());

            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Your contact is updated", "success"));

        }catch (Exception e){e.printStackTrace();}

        System.out.println("CONTACT NAME "+contact.getName());
        System.out.println("CONTACT ID "+contact.getCid());
        return "redirect:/user/"+contact.getCid()+"/contact";
    }

    //Your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }

    //Open Setting handler
    @GetMapping("/settings")
    public String openSetting(){
        return "normal/settings";
    }

    //Change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword")String oldPassword, @RequestParam("newPassword")String newPassword, Principal principal, HttpSession session){

        System.out.println("OLD PASSWORD "+oldPassword);
        System.out.println("NEW PASSWORD "+newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())){
            //change the password
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);

            session.setAttribute("message", new Message("Your password is successfully changed", "success"));

        }else {
            //error
            session.setAttribute("message", new Message("Wrong password", "danger"));
            return "redirect:/user/settings";

        }

        return "redirect:/user/index";

    }

    //Creating order for payment
    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception {

        System.out.println(data);
        int amt = Integer.parseInt(data.get("amount").toString());

        var client = new RazorpayClient("<KEY>", "<SECRET>");

        JSONObject options = new JSONObject();
        options.put("amount", amt*100);
        options.put("currency", "INR");
        options.put("receipt", "txn_123456");

        //creating  new order
        Order order = client.Orders.create(options);
        System.out.println(order);

        //save this data in your database

        MyOrder myOrder = new MyOrder();

        myOrder.setAmount(order.get("amount")+"");
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("created");
        myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
        myOrder.setReceipt(order.get("receipt"));

        this.myOrderRepository.save(myOrder);


        return order.toString();
    }

    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){

        MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
        myOrder.setPaymentId(data.get("payment_id").toString());
        myOrder.setStatus(data.get("status").toString());

        this.myOrderRepository.save(myOrder);


        System.out.println(data);
        return ResponseEntity.ok(Map.of("msg", "updated"));
    }
}
