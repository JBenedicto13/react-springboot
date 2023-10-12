import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Signin from '../Signin';
import Signup from '../Signup';
import PrivateRoute from './PrivateRoute';
import Dashboard from '../Dashboard';

const Routing = ({user}) => {
  return (
    <Routes>
        <Route path='/' element={<Signin />} />
        <Route path='/signin' element={<Signin />} />
        <Route path='/signup' element={<Signup />} />
        
        <Route element={<PrivateRoute user={user} />}>
            <Route path='/dashboard' element={<Dashboard />} />
        </Route>
    </Routes>
  )
}

export default Routing