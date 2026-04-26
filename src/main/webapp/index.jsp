<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Learn-ED - AI-Powered Learning</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <script src="https://unpkg.com/feather-icons"></script>

    <style>
        /* --- Global Settings & Variables (Teal Theme) --- */
        :root {
            --primary: #2ECC71;
            --primary-dark: #27AE60;
            --hero-bg-start: #235c75;
            --hero-bg-end: #0e848f;
            --nav-bg: rgba(255, 255, 255, 0.1);
            --text-hero: #FFFFFF;
            --text-dark: #34495E;
            --text-light: #7F8C8D;
            --white: #FFFFFF;
            --light-gray: #ecf0f1;
            --shadow: rgba(0, 0, 0, 0.15);
        }

        /* --- Landing Page Specific Styles --- */
        .landing-body {
            font-family: 'Poppins', sans-serif; /* Add font-family here since it's self-contained */
            background-color: var(--hero-bg-start);
            color: var(--text-dark);
            overflow-x: hidden;
        }

        .nav-container, .hero-content, .features-container, .landing-footer p {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 2rem;
        }

        .landing-nav {
            padding: 1.5rem 0;
            position: relative;
            z-index: 100;
        }

        .landing-nav .nav-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .nav-logo {
            font-weight: 700;
            font-size: 1.5rem;
            text-decoration: none;
            color: var(--text-hero);
        }

        .nav-links a {
            text-decoration: none;
            color: var(--text-hero);
            margin-left: 2rem;
            font-weight: 400;
            transition: color 0.3s;
        }
        
        .nav-links a:hover { color: var(--primary); }

        .btn {
            display: inline-block;
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: 8px;
            text-align: center;
            text-decoration: none;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .btn-primary { background-color: var(--primary); color: var(--white); }
        .btn-primary:hover { background-color: var(--primary-dark); transform: scale(1.02); }
        .btn-secondary { background-color: transparent; border: 1px solid var(--text-hero); color: var(--text-hero); }
        .btn-secondary:hover { background-color: rgba(255, 255, 255, 0.1); transform: scale(1.02); }

        .btn.btn-nav.btn-outline {
            background-color: transparent;
            border: 1px solid var(--text-hero);
            color: var(--text-hero);
            padding: 0.6rem 1.5rem;
        }
        .btn.btn-nav.btn-outline:hover {
            background-color: rgba(255, 255, 255, 0.1);
            border-color: var(--primary);
            color: var(--primary);
        }

        .hero-section {
            position: relative;
            display: flex;
            align-items: center;
            min-height: calc(100vh - 80px);
            overflow: hidden;
            background: linear-gradient(135deg, var(--hero-bg-start) 0%, var(--hero-bg-end) 100%);
            color: var(--text-hero);
            text-align: left;
        }

        .hero-content {
            position: relative;
            z-index: 10;
            max-width: 600px;
            padding: 4rem 0;
        }

        .hero-main-title {
            font-size: clamp(2.5rem, 6vw, 4.5rem);
            font-weight: 700;
            line-height: 1.1;
            margin-bottom: 0.5rem;
        }

        .hero-sub-title {
            font-size: clamp(1.5rem, 4vw, 2.5rem);
            font-weight: 500;
            color: rgba(255, 255, 255, 0.9);
            margin-bottom: 1rem;
        }

        .hero-description {
            font-size: clamp(1rem, 2vw, 1.15rem);
            color: rgba(255, 255, 255, 0.8);
            max-width: 500px;
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .hero-actions {
            display: flex;
            gap: 1rem;
        }

        .features-section {
            padding: 5rem 0;
            background-color: var(--white);
            color: var(--text-dark);
            text-align: center;
        }
        
        .features-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 2rem;
        }

        .feature-card {
            background-color: var(--light-gray);
            padding: 2.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        
        .feature-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }
        
        .feature-card svg { width: 50px; height: 50px; color: var(--primary); margin-bottom: 1.5rem; }
        .feature-card h3 { font-size: 1.3rem; margin-bottom: 0.8rem; color: var(--text-dark); }
        .feature-card p { color: var(--text-light); font-size: 0.95rem; }

        .landing-footer {
            text-align: center;
            padding: 2rem 0;
            background-color: var(--text-dark);
            color: rgba(255, 255, 255, 0.7);
        }
    </style>
</head>
<body class="landing-body">

    <nav class="landing-nav">
        <div class="nav-container">
            <a href="#" class="nav-logo">LEARN-ED</a>
            <div class="nav-links">
                <a href="#about">About</a>
                <a href="#contact">Contact</a>
                <a href="jsp/login.jsp" class="btn btn-nav btn-outline">Sign In</a>
            </div>
        </div>
    </nav>

    <header class="hero-section">
        <div class="hero-bg"></div> <div class="hero-content">
            <h1 class="hero-main-title">LEARN-ED</h1>
            <h2 class="hero-sub-title">AI-Powered Learning Platform</h2>
            <p class="hero-description">
                Unlock your full potential with personalized courses and smart quizzes. Our AI adapts to you, making learning efficient and engaging.
            </p>
            <div class="hero-actions">
                <a href="jsp/register.jsp" class="btn btn-primary">Try Now</a>
                <a href="#features" class="btn btn-secondary">Learn More</a>
            </div>
        </div>
    </header>

    <section id="features" class="features-section">
        <div class="features-container">
            <div class="feature-card">
                <i data-feather="cpu"></i>
                <h3>AI Course Generation</h3>
                <p>Input any topic, and our AI instantly crafts a comprehensive course with lessons and study materials tailored for you.</p>
            </div>
            <div class="feature-card">
                <i data-feather="edit"></i>
                <h3>Dynamic Quizzes</h3>
                <p>Challenge yourself with adaptive quizzes generated from your course material, pinpointing areas for improvement.</p>
            </div>
            <div class="feature-card">
                <i data-feather="download-cloud"></i>
                <h3>Offline Access</h3>
                <p>Download your personalized courses as PDFs to continue learning on the go, without an internet connection.</p>
            </div>
            <div class="feature-card">
                <i data-feather="award"></i>
                <h3>Track Your Progress</h3>
                <p>Monitor your learning journey with detailed performance statistics and insights on your profile page.</p>
            </div>
        </div>
    </section>

    <footer class="landing-footer">
        <p>&copy; 2025 LEARN-ED. All rights reserved.</p>
    </footer>

    <script>
      feather.replace();
    </script>
</body>
</html>