👤 Omar – Introduction and Context (50 seconds) 
Good morning, everyone. Today, we’re pleased to present an innovative system for automating identification processes. In many environments—such as financial institutions, government services, or access control—there is a constant need to verify identity documents quickly and reliably. Our team has developed a solution that uses computer vision to identify and validate Mexican INEs in real time, combining advanced image processing techniques with artificial intelligence. To explain how it works technically, I’ll hand it over to [Braulio].

👤 Tello – Technical Architecture (60 seconds) 
The system is composed of three main modules that work together. First, the capture module uses OpenCV to obtain real-time video. Then, a series of filters are applied: grayscale conversion, blurring to reduce noise, and adaptive thresholding to enhance contrast. 
The core of the system is the Tesseract OCR engine, specifically configured to recognize Spanish text. Finally, the validator searches for specific patterns such as NAME, ADDRESS, CURP, and DATE OF BIRTH. To show you the system in action, Cesar will now perform a live demonstration.

👤 Cesar – Live Demonstration (70 seconds) 
Now let’s see the system in action. Here we have the interface actively capturing real-time video. You can see the green rectangle that marks the optimal capture area. I’ll position the document within this frame... By pressing the S key, the system processes the current frame. We immediately apply the preprocessing filters that [Braulio] mentioned. In the console, we can see the extracted text after cleaning... and now... confirmation! The system has correctly identified all four required fields. This multi-field validation gives us high reliability in recognition. [Raul] will now explain what makes our solution special.

👤 Raul – Value Proposition and Innovation (60 seconds) What sets our system apart are four key innovations: 
One: Real-time processing that eliminates the need for static scans. 
Two: Specialized configuration for Mexican documents—it’s not a generic solution. 
Three: Accessibility—it works with any standard webcam. 
And four: Adaptive algorithms that adjust to different lighting conditions. We’ve achieved 85% accuracy in controlled conditions, with processing times under 100 milliseconds per frame. To conclude, [Alonso] will share the most valuable lessons from this project.

👤 Alonso – Skills and Conclusions (50 seconds) This project was much more than software development; it was a comprehensive learning experience. We developed general skills like effective teamwork and technical communication, but also career-specific skills: software engineering, digital image processing, and intelligent systems. We’ve shown that it’s possible to create accessible solutions using open-source technologies, bringing classroom theory into real-world applications. On behalf of the entire team, thank you for your attention.
