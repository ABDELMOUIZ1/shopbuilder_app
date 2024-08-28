import React, { useEffect, useState } from 'react';
import './Dashboard.css';
import Shop1 from '../../assets/Shop1.jpg';
import Shop2 from '../../assets/Shop2.jpg';
import Shop3 from '../../assets/Shop3.jpg';
import img1 from "../../assets/pic1.png";
import LinkedIn from '../../assets/LinkedIn.png';


const images = [
    Shop1, // Replace with your image paths
    Shop2,
    Shop3,
    // Add more images as needed
];


const Dashboard = () => {


    const [currentIndex, setCurrentIndex] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
        }, 3000); // Change image every 3 seconds

        return () => clearInterval(interval);
    }, []);

    const getIndexClass = (index) => {
        if (index === currentIndex) return 'center';
        if (index === (currentIndex + 1) % images.length) return 'right';
        if (index === (currentIndex - 1 + images.length) % images.length) return 'left';
        return '';
    };



    const words = ["You Need To Boost Your Business", "You Need More Exposure", "You Need Digital Presence","We Got You!!"];
    const [currentWord, setCurrentWord] = useState("");
    const [isDeleting, setIsDeleting] = useState(false);
    const [wordIndex, setWordIndex] = useState(0);
    const [typingSpeed, setTypingSpeed] = useState(200);

    useEffect(() => {
        const handleType = () => {
            const fullText = words[wordIndex];

            if (isDeleting) {
                setCurrentWord(fullText.substring(0, currentWord.length - 1));
                setTypingSpeed(100);
            } else {
                setCurrentWord(fullText.substring(0, currentWord.length + 1));
                setTypingSpeed(200);
            }

            if (!isDeleting && currentWord === fullText) {
                setTypingSpeed(2000);
                setIsDeleting(true);
            } else if (isDeleting && currentWord === "") {
                setIsDeleting(false);
                setWordIndex((prevIndex) => (prevIndex + 1) % words.length);
                setTypingSpeed(500);
            }
        };

        const timer = setTimeout(handleType, typingSpeed);
        return () => clearTimeout(timer);
    }, [currentWord, isDeleting, typingSpeed, wordIndex, words]);

    return(
        <><><><section id='Intro'>
            <div className='introContent'>
                <h1 className='hello'>Hello,</h1>
                <span className='introText'>You're a <span className='introName'>Business Owner</span> <br /><span className='changing-text'>{currentWord}</span></span>
            </div>
            <div className="carousel">
                {images.map((image, index) => (
                    <div
                        key={index}
                        className={`carousel-image ${getIndexClass(index)}`}
                        style={{ backgroundImage: `url(${image})` }} />
                ))}
            </div>


        </section><section className="avantagecont">
            <div className="secTitle">
                <h2 className="title" data-aos="fade-right">
                    Why <br />  Shop Builder ?
                </h2>
            </div>

            <div class="container">
                <div class="left" data-aos="fade-right">
                    <ul>
                        <li>. Planification and research</li>
                        <li>. Domain Registration</li>
                        <li>. Hosting</li>
                        <li>. Design and development</li>
                        <li>. Functionalities Configuration</li>
                        <li>. Test and security</li>
                    </ul>
                </div>
                <div class="middle" data-aos="fade-right">
                    <span>Or</span>
                </div>
                <div class="right" data-aos="fade-right">
                    <ul>
                        <li>. Register, and login</li>
                        <li>. Fill up a simple form</li>
                        <li>. Your Website is Ready to Go!</li>
                    </ul>
                    <img src={img1} alt='img' className="imgqst"></img>
                </div>
            </div>
            <div className="btnContainer">
                <button data-aos="fade-up" className="btnavt"><a href="#">Create your E-shop !</a></button>
            </div>
        </section></><section id='templates'>
            <h2 className='templatesTitle'>Website templates that set you up for success</h2>
            <span className='templatesDesc'>Get a headstart on your journey with customizable website templates, strategically researched and tailored for every industry.</span>
            <div className="horizontal-scroll-container">
                <div className="horizontal-scroll-content">
                    <img src={Shop1} alt="Image 1" className="scroll-image" />
                    <img src={Shop2} alt="Image 2" className="scroll-image" />
                    <img src={Shop3} alt="Image 3" className="scroll-image" />
                    {/* Add more images as needed */}
                </div>
            </div>


        </section></><section id='ContactPage'>
            <h2 className='contactTitle'>Contact Us:</h2>
            <span className='contactDesc'>Please feel free to fill out the form below to contact us.</span>
            <form className='contactForm'>
                <input type='Text' className='name' placeholder='Your Name' name='your_name' />
                <input type='email' className='email' placeholder='Your Email' name='your_email' />
                <textarea name='message' rows='5' placeholder='Your Message' className='msg'></textarea>
                <button  type='submit' value='send' className='submitBtn'><span>Submit</span></button>
                <div className='Links'>
                    <a href='' target='_blank'>
                        <img src={LinkedIn} alt='LinkedIn' className='LinkedInImg' />
                    </a>
                </div>
            </form>

        </section></>

    )
}


export default Dashboard;