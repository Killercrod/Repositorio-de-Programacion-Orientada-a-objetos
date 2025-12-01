# 📌 Comparative Script – Project Evolution (≈2 minutes)

## 🎤 General Presentation
**"Good morning. Today we will explain how our project evolved from the first delivery to the second one, highlighting the most important technical and functional improvements."**

---

## 1. From a console prototype to a complete web system (30s)

In the first delivery, we presented a functional prototype that operated entirely through the console.  
The system:

- Captured video using OpenCV  
- Processed images with filters (grayscale, blur, adaptive thresholding)  
- Extracted text with Tesseract  
- Validated key fields such as **Name, CURP, Address, and Date of Birth**  

It was a solid tool, but limited to a technical flow without a visual interface or data persistence.

In the second delivery, we made a major leap forward:  
✔ We developed a **professional web interface** using HTML, CSS, and JavaScript  
✔ We improved the user experience  
✔ The system became accessible without relying on console execution

---

## 2. Architectural evolution: from a monolithic script to MVC with persistence (35s)

In the first delivery, most of the logic lived in a single workflow: video capture, preprocessing, OCR, and validation.

In the second delivery, we fully implemented the **MVC pattern**:

- **Model:** Hibernate entities such as `INE` and `Usuario`  
- **View:** HTML + JavaScript web interfaces  
- **Controller:** Servlets handling HTTP requests  

We also integrated an **H2 database** using Hibernate, which allowed us to:

- Persist records  
- Meet requirements such as **RNF-02 (duplicate prevention)**  
- Support a complete flow from view → controller → persistent model

---

## 3. Requirements: from basic to refined and fully traceable (30s)

First delivery → Only minimal functional requirements: capture, processing, and field identification.

Second delivery → Requirements were refined and formalized:

- User stories with acceptance criteria  
- A `CURPValidator` class directly tied to RF-03  
- Prioritization using the **MoSCoW method**:  
  - MUST: Real-time validation  
  - SHOULD: Responsive web interface  
  - COULD: Database persistence  
- Updated **class diagram** including inheritance and composition  
- A fully traceable end-to-end process across all layers

---

## 4. Process and Competencies (25s)

First delivery → Focused mainly on image processing and recognition.

Second delivery → Expanded into full software engineering:

- MVC pattern  
- Hibernate and data persistence  
- Requirements engineering  
- Version control and collaboration via GitHub  
- Weekly sprints  
- Contribution metrics tracked using GitHub Projects  

This strengthened competencies such as teamwork, technical documentation, and architectural design.

---

## 🎯 Conclusion (10s)

**"It is important to clarify that from the planning of the project up to the implementation of the first delivery, the project did not experience significant changes, since the core product from the beginning was the Python script itself. It was only starting from the second delivery that we began planning and implementing a user interface using HTML."**
