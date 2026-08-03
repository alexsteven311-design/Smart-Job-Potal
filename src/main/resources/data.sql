-- Smart Job Portal - Comprehensive Job Listings
-- Real companies across all 28 states of India
-- Categories: Software, Data, Cloud, Mobile, Design, QA, Management, Finance, HR, Marketing, Sales, Healthcare, Education, Legal, Logistics, Manufacturing
MERGE INTO job (id, title, company, location, description, role, required_experience_years, posted_at, remote)
KEY(id)
VALUES

-- ===================== ANDHRA PRADESH =====================
(1,  'Data Analyst', 'Wipro', 'Visakhapatnam, Andhra Pradesh', 'Analyze business data and generate insights using Python and Power BI.', 'Data Analyst', 2, '2026-06-01 09:00:00', 0),
(2,  'Java Developer', 'Tech Mahindra', 'Amaravati, Andhra Pradesh', 'Develop enterprise Java applications and REST APIs using Spring Boot.', 'Java Developer', 3, '2026-06-01 10:00:00', 0),
(3,  'HR Executive', 'Dr. Reddy''s Laboratories', 'Visakhapatnam, Andhra Pradesh', 'Handle end-to-end recruitment, onboarding and employee engagement activities.', 'HR Executive', 2, '2026-06-01 11:00:00', 0),
(4,  'Sales Executive', 'HDFC Bank', 'Vijayawada, Andhra Pradesh', 'Drive retail banking product sales and acquire new customers in assigned territory.', 'Sales Executive', 1, '2026-06-01 12:00:00', 0),

-- ===================== ARUNACHAL PRADESH =====================
(5,  'IT Support Engineer', 'BSNL', 'Itanagar, Arunachal Pradesh', 'Provide IT infrastructure support and network maintenance for telecom operations.', 'IT Support Engineer', 1, '2026-06-01 13:00:00', 0),
(6,  'Teacher - Computer Science', 'Kendriya Vidyalaya', 'Itanagar, Arunachal Pradesh', 'Teach computer science subjects to classes 9 to 12 as per CBSE curriculum.', 'Teacher', 2, '2026-06-01 14:00:00', 0),

-- ===================== ASSAM =====================
(7,  'Business Analyst', 'Infosys BPM', 'Guwahati, Assam', 'Gather requirements, create BRDs and work with stakeholders on process improvements.', 'Business Analyst', 3, '2026-06-02 09:00:00', 0),
(8,  'Digital Marketing Specialist', 'OYO Rooms', 'Guwahati, Assam', 'Plan and execute SEO, SEM, social media and email marketing campaigns.', 'Digital Marketing Specialist', 2, '2026-06-02 10:00:00', 1),
(9,  'Accountant', 'Assam Tea Corporation', 'Guwahati, Assam', 'Manage accounts payable and receivable, prepare financial statements and GST filings.', 'Accountant', 3, '2026-06-02 11:00:00', 0),

-- ===================== BIHAR =====================
(10, 'Python Developer', 'Patna Digital Services', 'Patna, Bihar', 'Build data pipelines and automation scripts using Python and Pandas.', 'Python Developer', 2, '2026-06-02 12:00:00', 0),
(11, 'Bank PO', 'State Bank of India', 'Patna, Bihar', 'Handle branch banking operations, customer service and financial transactions.', 'Bank Officer', 0, '2026-06-02 13:00:00', 0),
(12, 'Civil Engineer', 'Bihar State Road Development Corporation', 'Patna, Bihar', 'Supervise road construction and infrastructure development projects.', 'Civil Engineer', 3, '2026-06-02 14:00:00', 0),

-- ===================== CHHATTISGARH =====================
(13, 'Full Stack Developer', 'NIC (National Informatics Centre)', 'Raipur, Chhattisgarh', 'Develop and maintain government web portals using Java and Angular.', 'Full Stack Developer', 3, '2026-06-03 09:00:00', 0),
(14, 'Mining Engineer', 'Coal India Limited', 'Bilaspur, Chhattisgarh', 'Plan and supervise underground and opencast mining operations safely.', 'Mining Engineer', 4, '2026-06-03 10:00:00', 0),
(15, 'HR Manager', 'Bhilai Steel Plant', 'Bhilai, Chhattisgarh', 'Manage HR operations, talent acquisition and employee welfare programs.', 'HR Manager', 6, '2026-06-03 11:00:00', 0),

-- ===================== GOA =====================
(16, 'Frontend Developer', 'Persistent Systems', 'Panaji, Goa', 'Build responsive web applications using Angular and React.', 'Frontend Developer', 2, '2026-06-03 12:00:00', 0),
(17, 'Hotel Operations Manager', 'Taj Hotels', 'Panaji, Goa', 'Oversee daily hotel operations, guest experience and staff management.', 'Operations Manager', 5, '2026-06-03 13:00:00', 0),
(18, 'Graphic Designer', 'DDB Mudra', 'Panaji, Goa', 'Create visual concepts for print, digital and social media campaigns.', 'Graphic Designer', 2, '2026-06-03 14:00:00', 1),

-- ===================== GUJARAT =====================
(19, 'Data Scientist', 'Tata Consultancy Services', 'Ahmedabad, Gujarat', 'Build ML models and predictive analytics solutions using Python and TensorFlow.', 'Data Scientist', 4, '2026-06-04 09:00:00', 0),
(20, 'SAP Consultant', 'Wipro', 'Surat, Gujarat', 'Implement and support SAP FICO modules for enterprise clients.', 'SAP Consultant', 5, '2026-06-04 10:00:00', 0),
(21, 'Financial Analyst', 'Adani Group', 'Ahmedabad, Gujarat', 'Perform financial modelling, valuation and investment analysis for group companies.', 'Financial Analyst', 3, '2026-06-04 11:00:00', 0),
(22, 'Supply Chain Manager', 'Torrent Pharmaceuticals', 'Ahmedabad, Gujarat', 'Manage end-to-end supply chain operations and vendor relationships.', 'Supply Chain Manager', 5, '2026-06-04 12:00:00', 0),
(23, 'UI/UX Designer', 'Jio Platforms', 'Surat, Gujarat', 'Design user interfaces for mobile and web products using Figma and Adobe XD.', 'UI/UX Designer', 3, '2026-06-04 13:00:00', 1),

-- ===================== HARYANA =====================
(24, 'Cloud Engineer', 'HCL Technologies', 'Gurugram, Haryana', 'Design and manage AWS cloud infrastructure, CI/CD pipelines and DevOps processes.', 'Cloud Engineer', 3, '2026-06-04 14:00:00', 1),
(25, 'Product Manager', 'Samsung R&D Institute', 'Gurugram, Haryana', 'Lead product roadmap and coordinate between engineering and business teams.', 'Product Manager', 5, '2026-06-05 09:00:00', 0),
(26, 'Legal Counsel', 'Maruti Suzuki', 'Gurugram, Haryana', 'Handle corporate legal matters, contracts and compliance for automotive operations.', 'Legal Counsel', 6, '2026-06-05 10:00:00', 0),
(27, 'Content Writer', 'IndiaMart', 'Gurugram, Haryana', 'Write SEO-optimised content for product pages, blogs and marketing materials.', 'Content Writer', 1, '2026-06-05 11:00:00', 1),

-- ===================== HIMACHAL PRADESH =====================
(28, 'Network Engineer', 'BSNL', 'Shimla, Himachal Pradesh', 'Configure and maintain telecom network infrastructure across the state.', 'Network Engineer', 2, '2026-06-05 12:00:00', 0),
(29, 'Pharmacist', 'AIIMS Bilaspur', 'Bilaspur, Himachal Pradesh', 'Dispense medications, counsel patients and manage pharmacy inventory.', 'Pharmacist', 2, '2026-06-05 13:00:00', 0),

-- ===================== JHARKHAND =====================
(30, 'Android Developer', 'Ranchi Tech Solutions', 'Ranchi, Jharkhand', 'Develop native Android applications using Kotlin and Jetpack Compose.', 'Android Developer', 2, '2026-06-05 14:00:00', 0),
(31, 'Electrical Engineer', 'NTPC Limited', 'Jamshedpur, Jharkhand', 'Maintain electrical systems and power plant equipment for thermal power generation.', 'Electrical Engineer', 3, '2026-06-06 09:00:00', 0),
(32, 'Operations Executive', 'Tata Steel', 'Jamshedpur, Jharkhand', 'Monitor and optimise steel plant production operations and quality control.', 'Operations Executive', 3, '2026-06-06 10:00:00', 0),

-- ===================== KARNATAKA =====================
(33, 'Machine Learning Engineer', 'Infosys', 'Bengaluru, Karnataka', 'Design and deploy ML models for enterprise AI platforms using Python and PyTorch.', 'ML Engineer', 4, '2026-06-06 11:00:00', 1),
(34, 'DevOps Engineer', 'Flipkart', 'Bengaluru, Karnataka', 'Manage Kubernetes clusters, CI/CD pipelines and cloud infrastructure on GCP.', 'DevOps Engineer', 3, '2026-06-06 12:00:00', 1),
(35, 'Data Engineer', 'Mu Sigma', 'Bengaluru, Karnataka', 'Build and maintain data warehouses and ETL pipelines using Spark and Hive.', 'Data Engineer', 3, '2026-06-06 13:00:00', 0),
(36, 'Product Designer', 'Swiggy', 'Bengaluru, Karnataka', 'Own end-to-end product design for consumer-facing features on the Swiggy app.', 'Product Designer', 4, '2026-06-06 14:00:00', 1),
(37, 'Talent Acquisition Lead', 'Wipro', 'Bengaluru, Karnataka', 'Lead bulk and lateral hiring for technology roles across business units.', 'Talent Acquisition', 5, '2026-06-07 09:00:00', 0),
(38, 'Finance Manager', 'Biocon', 'Bengaluru, Karnataka', 'Manage financial planning, budgeting and MIS reporting for pharma business.', 'Finance Manager', 6, '2026-06-07 10:00:00', 0),

-- ===================== KERALA =====================
(39, 'React Developer', 'UST Global', 'Thiruvananthapuram, Kerala', 'Develop modern React web applications with Redux and TypeScript.', 'Frontend Developer', 3, '2026-06-07 11:00:00', 1),
(40, 'QA Engineer', 'IBS Software', 'Kochi, Kerala', 'Perform manual and automated testing using Selenium and TestNG for aviation software.', 'QA Engineer', 2, '2026-06-07 12:00:00', 0),
(41, 'Nurse', 'Aster Hospitals', 'Kochi, Kerala', 'Provide patient care and assist physicians in ICU and general ward settings.', 'Nurse', 1, '2026-06-07 13:00:00', 0),
(42, 'Marketing Manager', 'Malabar Gold', 'Kozhikode, Kerala', 'Plan and execute brand marketing, promotions and retail marketing campaigns.', 'Marketing Manager', 5, '2026-06-07 14:00:00', 0),

-- ===================== MADHYA PRADESH =====================
(43, 'ERP Consultant', 'Infosys', 'Indore, Madhya Pradesh', 'Implement Oracle ERP solutions for manufacturing clients across India.', 'ERP Consultant', 4, '2026-06-08 09:00:00', 0),
(44, 'Software Engineer', 'TCS', 'Bhopal, Madhya Pradesh', 'Develop and maintain enterprise Java microservices for banking clients.', 'Software Engineer', 2, '2026-06-08 10:00:00', 0),
(45, 'Radiologist', 'AIIMS Bhopal', 'Bhopal, Madhya Pradesh', 'Interpret diagnostic imaging reports and provide clinical consultations.', 'Radiologist', 5, '2026-06-08 11:00:00', 0),
(46, 'Logistics Coordinator', 'VRL Logistics', 'Indore, Madhya Pradesh', 'Coordinate freight movement, manage delivery schedules and vendor relations.', 'Logistics Coordinator', 2, '2026-06-08 12:00:00', 0),

-- ===================== MAHARASHTRA =====================
(47, 'Data Analyst', 'KPMG India', 'Mumbai, Maharashtra', 'Deliver data analytics and visualization solutions using Tableau and SQL.', 'Data Analyst', 3, '2026-06-08 13:00:00', 0),
(48, 'Cybersecurity Analyst', 'Accenture', 'Pune, Maharashtra', 'Monitor and respond to security incidents, perform vulnerability assessments.', 'Cybersecurity Analyst', 3, '2026-06-08 14:00:00', 0),
(49, 'iOS Developer', 'Persistent Systems', 'Nagpur, Maharashtra', 'Build iOS applications using Swift and SwiftUI for fintech clients.', 'iOS Developer', 3, '2026-06-09 09:00:00', 1),
(50, 'Investment Banker', 'Morgan Stanley', 'Mumbai, Maharashtra', 'Execute M&A transactions, IPOs and debt capital market deals for corporate clients.', 'Investment Banker', 4, '2026-06-09 10:00:00', 0),
(51, 'Mechanical Engineer', 'Bajaj Auto', 'Pune, Maharashtra', 'Design and develop two-wheeler components and assemblies using CAD tools.', 'Mechanical Engineer', 3, '2026-06-09 11:00:00', 0),
(52, 'Customer Success Manager', 'Freshworks', 'Mumbai, Maharashtra', 'Manage enterprise customer accounts, drive adoption and reduce churn.', 'Customer Success Manager', 4, '2026-06-09 12:00:00', 1),
(53, 'Architect', 'Godrej Properties', 'Mumbai, Maharashtra', 'Design residential and commercial building layouts using AutoCAD and Revit.', 'Architect', 4, '2026-06-09 13:00:00', 0),

-- ===================== MANIPUR =====================
(54, 'Web Developer', 'NIC', 'Imphal, Manipur', 'Develop and maintain state government web portals using PHP and MySQL.', 'Web Developer', 1, '2026-06-09 14:00:00', 0),
(55, 'Sports Coach', 'Sports Authority of India', 'Imphal, Manipur', 'Train and mentor athletes in boxing and martial arts for national competitions.', 'Sports Coach', 5, '2026-06-10 09:00:00', 0),

-- ===================== MEGHALAYA =====================
(56, 'IT Analyst', 'Meghalaya IT Society', 'Shillong, Meghalaya', 'Analyse and improve IT systems for government digital services.', 'IT Analyst', 2, '2026-06-10 10:00:00', 0),
(57, 'Tourism Officer', 'Meghalaya Tourism', 'Shillong, Meghalaya', 'Promote tourism destinations, manage tour operations and travel packages.', 'Tourism Officer', 3, '2026-06-10 11:00:00', 0),

-- ===================== MIZORAM =====================
(58, 'System Administrator', 'BSNL', 'Aizawl, Mizoram', 'Manage Linux servers, backups and IT infrastructure for telecom operations.', 'System Administrator', 2, '2026-06-10 12:00:00', 0),
(59, 'Healthcare Administrator', 'Zoram Medical College', 'Aizawl, Mizoram', 'Manage hospital administration, patient records and regulatory compliance.', 'Healthcare Administrator', 3, '2026-06-10 13:00:00', 0),

-- ===================== NAGALAND =====================
(60, 'Software Developer', 'NIC', 'Kohima, Nagaland', 'Build e-governance portals and citizen service applications for state departments.', 'Software Developer', 2, '2026-06-10 14:00:00', 0),
(61, 'Agricultural Officer', 'Nagaland Dept. of Agriculture', 'Kohima, Nagaland', 'Advise farmers on modern farming techniques, crop protection and soil management.', 'Agricultural Officer', 3, '2026-06-11 09:00:00', 0),

-- ===================== ODISHA =====================
(62, 'Big Data Engineer', 'Infosys', 'Bhubaneswar, Odisha', 'Design Hadoop and Spark-based data processing pipelines for retail analytics.', 'Big Data Engineer', 4, '2026-06-11 10:00:00', 0),
(63, 'Angular Developer', 'Mindtree', 'Bhubaneswar, Odisha', 'Build enterprise Angular applications with NgRx state management.', 'Frontend Developer', 2, '2026-06-11 11:00:00', 1),
(64, 'Steel Plant Engineer', 'SAIL (Steel Authority of India)', 'Rourkela, Odisha', 'Monitor and maintain blast furnace and steel rolling mill operations.', 'Metallurgical Engineer', 3, '2026-06-11 12:00:00', 0),

-- ===================== PUNJAB =====================
(65, 'Backend Developer', 'Nagarro', 'Chandigarh, Punjab', 'Build scalable Node.js microservices and REST APIs for SaaS products.', 'Backend Developer', 3, '2026-06-11 13:00:00', 1),
(66, 'BI Developer', 'Capgemini', 'Mohali, Punjab', 'Design Power BI dashboards and reports for retail and logistics clients.', 'BI Developer', 3, '2026-06-11 14:00:00', 0),
(67, 'Branch Manager', 'Punjab National Bank', 'Ludhiana, Punjab', 'Oversee branch banking operations, credit appraisals and team management.', 'Branch Manager', 7, '2026-06-12 09:00:00', 0),
(68, 'Textile Designer', 'Vardhman Textiles', 'Ludhiana, Punjab', 'Design fabric patterns and colour combinations for apparel manufacturing.', 'Textile Designer', 3, '2026-06-12 10:00:00', 0),

-- ===================== RAJASTHAN =====================
(69, 'Cloud Architect', 'HCL Technologies', 'Jaipur, Rajasthan', 'Design multi-cloud architectures on AWS and Azure for enterprise clients.', 'Cloud Architect', 6, '2026-06-12 11:00:00', 1),
(70, 'Data Analyst', 'Genpact', 'Jaipur, Rajasthan', 'Perform data analysis and reporting using SQL, Excel and Power BI.', 'Data Analyst', 2, '2026-06-12 12:00:00', 0),
(71, 'Jewellery Designer', 'Titan Company', 'Jaipur, Rajasthan', 'Design gold and diamond jewellery collections using CAD and hand sketching.', 'Jewellery Designer', 3, '2026-06-12 13:00:00', 0),
(72, 'Hotel Manager', 'ITC Hotels', 'Jaipur, Rajasthan', 'Manage hotel operations, revenue management and guest experience.', 'Hotel Manager', 6, '2026-06-12 14:00:00', 0),

-- ===================== SIKKIM =====================
(73, 'IT Officer', 'Sikkim State IT Cell', 'Gangtok, Sikkim', 'Manage state IT infrastructure and support digital governance initiatives.', 'IT Officer', 2, '2026-06-13 09:00:00', 0),
(74, 'Ecotourism Guide', 'Sikkim Tourism', 'Gangtok, Sikkim', 'Guide trekking and eco-tourism groups across Sikkim Himalayan trails.', 'Tourism Guide', 2, '2026-06-13 10:00:00', 0),

-- ===================== TAMIL NADU =====================
(75, 'AI Engineer', 'Zoho Corporation', 'Chennai, Tamil Nadu', 'Build AI-powered features for Zoho products using NLP and deep learning.', 'AI Engineer', 4, '2026-06-13 11:00:00', 0),
(76, 'Java Architect', 'Cognizant', 'Chennai, Tamil Nadu', 'Design Java microservices architecture for large-scale banking systems.', 'Java Architect', 8, '2026-06-13 12:00:00', 0),
(77, 'Full Stack Developer', 'Freshworks', 'Chennai, Tamil Nadu', 'Develop Ruby on Rails and React features for CRM SaaS platform.', 'Full Stack Developer', 3, '2026-06-13 13:00:00', 1),
(78, 'Automobile Engineer', 'TVS Motor Company', 'Hosur, Tamil Nadu', 'Design and test two-wheeler powertrains and chassis components.', 'Automobile Engineer', 3, '2026-06-13 14:00:00', 0),
(79, 'Fashion Designer', 'Raymond', 'Coimbatore, Tamil Nadu', 'Design men''s apparel collections and manage production sampling process.', 'Fashion Designer', 3, '2026-06-14 09:00:00', 0),
(80, 'Legal Associate', 'Lakshmikumaran & Sridharan', 'Chennai, Tamil Nadu', 'Handle corporate litigation, contract drafting and IP matters.', 'Legal Associate', 2, '2026-06-14 10:00:00', 0),

-- ===================== TELANGANA =====================
(81, 'Data Scientist', 'Amazon', 'Hyderabad, Telangana', 'Build recommendation and forecasting models for Amazon supply chain.', 'Data Scientist', 5, '2026-06-14 11:00:00', 1),
(82, 'Solutions Architect', 'Microsoft', 'Hyderabad, Telangana', 'Design Azure-based enterprise solutions and lead technical pre-sales.', 'Solutions Architect', 7, '2026-06-14 12:00:00', 1),
(83, 'Software Engineer', 'Google', 'Hyderabad, Telangana', 'Build large-scale distributed systems in Go and Java for Google Cloud.', 'Software Engineer', 4, '2026-06-14 13:00:00', 1),
(84, 'Cybersecurity Engineer', 'Deloitte', 'Hyderabad, Telangana', 'Conduct penetration testing, threat modelling and security audits for enterprise clients.', 'Cybersecurity Engineer', 4, '2026-06-14 14:00:00', 1),
(85, 'Clinical Research Associate', 'Dr. Reddy''s Laboratories', 'Hyderabad, Telangana', 'Monitor clinical trials, manage site documentation and ensure GCP compliance.', 'Clinical Research Associate', 3, '2026-06-15 09:00:00', 0),
(86, 'Embedded Systems Engineer', 'Qualcomm', 'Hyderabad, Telangana', 'Develop firmware and embedded software for 5G chipsets in C and ARM Assembly.', 'Embedded Engineer', 4, '2026-06-15 10:00:00', 0),

-- ===================== TRIPURA =====================
(87, 'Web Developer', 'NIC', 'Agartala, Tripura', 'Develop government web portals and citizen service apps using Django and React.', 'Web Developer', 2, '2026-06-15 11:00:00', 0),
(88, 'Rubber Plantation Manager', 'Tripura Rubber Mission', 'Agartala, Tripura', 'Manage rubber estate operations, yield improvement and worker welfare.', 'Plantation Manager', 4, '2026-06-15 12:00:00', 0),

-- ===================== UTTAR PRADESH =====================
(89, 'Blockchain Developer', 'Tech Mahindra', 'Lucknow, Uttar Pradesh', 'Develop smart contracts and DApps using Solidity and Ethereum.', 'Blockchain Developer', 3, '2026-06-15 13:00:00', 1),
(90, 'Data Engineer', 'Wipro', 'Noida, Uttar Pradesh', 'Build real-time data streaming pipelines using Kafka and Apache Flink.', 'Data Engineer', 4, '2026-06-15 14:00:00', 0),
(91, 'Product Analyst', 'Paytm', 'Noida, Uttar Pradesh', 'Analyse product metrics and user behaviour to drive feature decisions.', 'Product Analyst', 3, '2026-06-16 09:00:00', 0),
(92, 'News Reporter', 'Dainik Jagran', 'Lucknow, Uttar Pradesh', 'Cover state political and crime news, file reports and manage field sources.', 'Journalist', 2, '2026-06-16 10:00:00', 0),
(93, 'MBBS Doctor', 'Ram Manohar Lohia Hospital', 'Lucknow, Uttar Pradesh', 'Provide medical consultation and treatment in general medicine OPD and wards.', 'Medical Doctor', 1, '2026-06-16 11:00:00', 0),
(94, 'Operations Manager', 'Amazon Fulfillment', 'Agra, Uttar Pradesh', 'Manage warehouse operations, shift planning and last-mile delivery operations.', 'Operations Manager', 5, '2026-06-16 12:00:00', 0),

-- ===================== UTTARAKHAND =====================
(95, 'Software Engineer', 'HCL Technologies', 'Dehradun, Uttarakhand', 'Develop C++ based embedded software for automotive clients globally.', 'Software Engineer', 2, '2026-06-16 13:00:00', 0),
(96, 'Ayurvedic Doctor', 'Patanjali Ayurved', 'Haridwar, Uttarakhand', 'Provide Ayurvedic consultations and treatment at wellness centres.', 'Ayurvedic Doctor', 2, '2026-06-16 14:00:00', 0),
(97, 'Adventure Tourism Manager', 'Camp Terraces', 'Rishikesh, Uttarakhand', 'Manage white-water rafting and trekking operations and guide teams.', 'Tourism Manager', 4, '2026-06-17 09:00:00', 0),

-- ===================== WEST BENGAL =====================
(98,  'ML Engineer', 'TCS', 'Kolkata, West Bengal', 'Develop and deploy machine learning models for banking fraud detection.', 'ML Engineer', 4, '2026-06-17 10:00:00', 0),
(99,  'BI Analyst', 'Capgemini', 'Kolkata, West Bengal', 'Design and maintain BI reports using SAP BusinessObjects and Tableau.', 'BI Analyst', 3, '2026-06-17 11:00:00', 0),
(100, 'React Native Developer', 'Cognizant', 'Kolkata, West Bengal', 'Build cross-platform mobile apps using React Native for e-commerce clients.', 'Mobile Developer', 3, '2026-06-17 12:00:00', 1),
(101, 'Chartered Accountant', 'PwC India', 'Kolkata, West Bengal', 'Conduct statutory audits, tax advisory and financial due diligence for clients.', 'Chartered Accountant', 3, '2026-06-17 13:00:00', 0),
(102, 'Jute Export Manager', 'Gloster Jute Mills', 'Kolkata, West Bengal', 'Manage international jute product exports, pricing and buyer relationships.', 'Export Manager', 5, '2026-06-17 14:00:00', 0),
(103, 'Game Developer', 'Nazara Technologies', 'Kolkata, West Bengal', 'Develop mobile games using Unity3D and C# for Indian and global markets.', 'Game Developer', 3, '2026-06-18 09:00:00', 1);

-- ===================== POSTS FEED =====================
MERGE INTO posts (id, category, title, content, author_name, author_role, company, tags, likes, comments, shares, created_at)
KEY(id)
VALUES
(1, 'NEWS',    'TCS announces 40,000 fresher hires for FY2026',
 'Tata Consultancy Services has confirmed plans to onboard 40,000 fresh graduates in FY2026, focusing on AI, cloud, and full-stack roles. The company is partnering with IITs and NITs for early talent pipelines.',
 'Priya Sharma', 'Tech Journalist', 'TCS', 'TCS,hiring,freshers,IT', 312, 47, 0, '2026-06-10 09:00:00'),

(2, 'NEWS',    'Infosys opens new AI Centre of Excellence in Hyderabad',
 'Infosys has inaugurated a 500-seat AI Centre of Excellence in Hyderabad, dedicated to generative AI, LLM fine-tuning, and enterprise automation. The centre will collaborate with Microsoft and NVIDIA.',
 'Rahul Verma', 'Business Reporter', 'Infosys', 'Infosys,AI,Hyderabad,GenAI', 289, 38, 0, '2026-06-11 10:00:00'),

(3, 'NEWS',    'Wipro to upskill 1 lakh employees in Generative AI by 2027',
 'Wipro has launched an internal GenAI Academy targeting 1,00,000 employees across service lines. The programme covers prompt engineering, RAG pipelines, and responsible AI practices.',
 'Ananya Iyer', 'HR Correspondent', 'Wipro', 'Wipro,GenAI,upskilling,training', 198, 29, 0, '2026-06-12 08:30:00'),

(4, 'NEWS',    'Flipkart Commerce Cloud expands to Southeast Asia',
 'Flipkart''s B2B commerce platform is expanding to Singapore and Indonesia, creating 200+ new tech roles in backend engineering, data science, and product management.',
 'Karan Mehta', 'Startup Desk', 'Flipkart', 'Flipkart,expansion,ecommerce,jobs', 145, 22, 0, '2026-06-13 11:00:00'),

(5, 'HIRING',  'Google Hyderabad is hiring — 150+ open roles in 2026',
 'Google India is actively hiring for Software Engineers (L4/L5), Data Scientists, and Site Reliability Engineers at its Hyderabad campus. Roles span Google Cloud, YouTube, and Search. Apply via careers.google.com.',
 'Sneha Pillai', 'Recruiter at Google', 'Google', 'Google,hiring,Hyderabad,SWE', 521, 93, 0, '2026-06-09 09:00:00'),

(6, 'HIRING',  'Amazon India opens 3,000 tech positions across Bangalore and Hyderabad',
 'Amazon is hiring for SDE-II, SDE-III, Data Engineers, and ML Scientists across its India offices. Focus areas include AWS, Alexa, and Supply Chain tech. Referrals welcome — DM for JD links.',
 'Vikram Nair', 'Senior SDE at Amazon', 'Amazon', 'Amazon,AWS,hiring,Bangalore', 634, 112, 0, '2026-06-10 14:00:00'),

(7, 'HIRING',  'Zoho is hiring freshers — No bond, No service agreement',
 'Zoho Corporation is hiring 2024/2025 graduates for software development roles across Chennai and Tenkasi. No bond, no service agreement. Package: 6–8 LPA. Apply at careers.zoho.com.',
 'Divya Krishnan', 'HR at Zoho', 'Zoho', 'Zoho,freshers,Chennai,noagrement', 892, 204, 0, '2026-06-11 09:00:00'),

(8, 'HIRING',  'Swiggy hiring 500 engineers for Instamart and Snacc expansion',
 'Swiggy is scaling its quick-commerce and social commerce teams. Open roles: Backend (Go/Java), iOS, Android, Data Engineering, and ML. Hybrid work from Bangalore. Referral bonus: ₹50,000.',
 'Arjun Reddy', 'Engineering Manager at Swiggy', 'Swiggy', 'Swiggy,hiring,quickcommerce,Bangalore', 447, 78, 0, '2026-06-12 10:00:00'),

(9, 'SUCCESS', 'From 3 rejections to Google L5 — My 8-month journey',
 'I was rejected by Amazon, Microsoft, and Flipkart in 2025. Instead of giving up, I spent 8 months grinding LeetCode, system design, and behavioural prep. Last month I received an L5 offer from Google Hyderabad at 52 LPA. The key? Consistency over intensity. Happy to share my prep resources in the comments.',
 'Aditya Bose', 'Software Engineer at Google', 'Google', 'success,google,L5,journey,leetcode', 1243, 318, 0, '2026-06-08 08:00:00'),

(10, 'SUCCESS', 'Placed at Microsoft after 2 years of gap — Here''s what worked',
 'I took a 2-year career break to care for my family. Returning to tech felt impossible. I started with free AWS and Azure certifications, rebuilt my GitHub, and applied to 60+ companies. Microsoft called after 3 months. My advice: your gap is not your identity. Keep building.',
 'Meera Joshi', 'Cloud Engineer at Microsoft', 'Microsoft', 'success,microsoft,careerbreak,cloud', 987, 241, 0, '2026-06-09 07:30:00'),

(11, 'SUCCESS', 'Tier-3 college to Infosys to Flipkart — My 4-year story',
 'I graduated from a tier-3 college in Bihar with a 6.8 CGPA. Got into Infosys as a fresher, worked hard for 2 years, cleared Flipkart''s SDE-1 interview, and now I''m at SDE-2 level. Your college is your starting point, not your ceiling. DM me if you want my interview prep plan.',
 'Rohit Kumar', 'SDE-2 at Flipkart', 'Flipkart', 'success,flipkart,tier3,sde', 1567, 402, 0, '2026-06-07 09:00:00'),

(12, 'SUCCESS', 'Got my first Data Science job at 28 after switching from teaching',
 'I was a school teacher for 5 years. At 27, I started learning Python, statistics, and ML on YouTube and Kaggle. One year later, I cracked an interview at Mu Sigma, Bangalore. If you are thinking of switching careers — start today, not tomorrow.',
 'Lakshmi Nair', 'Data Scientist at Mu Sigma', 'Mu Sigma', 'success,careerswitch,datascience,python', 2103, 567, 0, '2026-06-06 10:00:00');
