import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Signin from '../Signin';
import Signup from '../Signup';
import Dashboard from '../Dashboard';

const Routing = () => {
  return (
    <Routes>
        <Route path='/' element={<Signin />} />
        <Route path='/signin' element={<Signin />} />
        <Route path='/signup' element={<Signup />} />
        <Route path='/dashboard' element={<Dashboard />} />
    </Routes>
  )
}

export default Routing