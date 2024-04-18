import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const Register = ({ROLE}) => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nameError, setNameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  console.log({ROLE})
  const handleNameChange = (e) => {
    
    const formattedName = e.target.value
      .trim()
      .split(' ')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
    setName(formattedName);
    validateName(formattedName);
  };

  const validateName = (name) => {
    const namePattern = /^[a-zA-Z\s]+$/;
  


   
    if(name==""){
      setNameError('')
    }
    else if (!namePattern.test(name)) {
      setNameError('Name should contain only alphabetical characters');
    } else {
      setNameError('');
    }
 
  };

  const validateEmail = (email) => {
    const emailPattern = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if(email==""){
      setEmailError('')
    }
   else  if (!emailPattern.test(email)) {
      setEmailError('Invalid email format');
    } else {
      setEmailError('');
    }
  };

  const validatePassword = (password) => {
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
    if (!passwordPattern.test(password)) {
      if(password==""){
        setPasswordError('')
      }
      else{
      setPasswordError(
        'Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character'
      );
    }
    } 
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    validateName(name);
    validateEmail(email);
    validatePassword(password);

    if (nameError === '' && emailError === '' && passwordError === '') {
      // Submit form data
      console.log('Form submitted successfully!');
      setName('');
      setEmail('');
      setPassword('');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100 shadow-lg">
      <div className='justify-center bg-blue-500 w-1/5 shadow-md rounded-sm'>
        <h1 className='mt-8 px pl-10 text-white text-3xl '>Looks like you're new here!</h1>
        <p className='mt-8 pl-10 pr-10 text-rose-100 text-lg'>Sign up with your mobile number to get started</p>
        <p className="mt-6 pl-10 pr-10 text-rose-100 text-lg">
                {ROLE === "SELLER"
                  ? "Register to access dashboard, list products and  orders."
                  : "Register to shop, order and access cart."}
              </p>
        <img className=" pl-12 mt-20 mb-8"
 src="/src/Images/login bottom logo.png" alt="images"  />
      </div>
    
      <form onSubmit={handleSubmit} className='bg-white w-1/3 shadow-md rounded-sm p-10 h-full '>
        <div className='relative mt-5'>
         
          <input
          autoComplete='off'
          className=" peer placeholder-transparent h-10 w-full border-b-2 focus:outline-none "
            type="text"
            id="name"
            name="name"
            value={name}
            onChange={handleNameChange}
           
          />
           <label htmlFor="name" className="absolute left-0  text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 
              peer-placeholder-shown:top-2 transition-all peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm">Enter Your Name Here:</label>
          <div className='  h-6' >{nameError && <div className="error text-red-600 text-xs">{nameError} </div>}
          </div>
        </div>

        <div className='relative mt-5'>
          
          <input
           autoComplete='off'
           className=" peer placeholder-transparent h-10 w-full border-b-2 focus:outline-none "
           type="email"
            id="email"
            name="email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              validateEmail(e.target.value);
            }}
           
         
            
            required
          />
          <label htmlFor="email" className="absolute left-0  text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 hover:cursor-pointer
              peer-placeholder-shown:top-2 transition-all peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm">Enter the email:</label>
         <div  className='  h-10'>
          {emailError && <div className="error text-xs text-red-600"> {emailError} </div>}
        </div>
        </div>

        <div className='relative mt-1'>
          
          <input
           autoComplete='off'
           className=" peer placeholder-transparent h-10 w-full border-b-2 focus:outline-none "
            type="password"
            id="password"
            name="password"
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              validatePassword(e.target.value);
            }}
            required
          />
          <label htmlFor="password" className="absolute left-0  text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 hover:cursor-pointer
              peer-placeholder-shown:top-2 transition-all peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm">Enter the Password:</label>
              <div className='  h-10'>
          {passwordError && <div className=" text-xs text-red-600">{passwordError} </div>}
          </div>


        </div>
        <div className=' text-xs mb-5 input-focus:translate-y-20'>By continuing, you agree to Filpkart's <a className='text-blue-500' href="">Terms</a>  and <a className='text-blue-500' href="">Privicy Policy</a></div>


        <button className='bg-orange-600 rounded-xl p-3 text-white w-full 'n type="submit">Submit</button>
      <Link to={'/login'}>
        <button className={" bg-white border-b-2 rounded-xl p-3 text-blue-800 shadow-md font-bold w-full mt-7 mb-6 "} type="submit">Existing User?Log in</button> 
        </Link>
      </form>
    </div>
  );
};

export default Register;