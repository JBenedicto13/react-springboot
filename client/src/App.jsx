import './App.css';
import { BrowserRouter } from 'react-router-dom';
import Routing from './components/routing/Routing';
import Box from '@mui/material/Box';
function App() {

  return (
    <BrowserRouter>
      <Box>
        <Box className="main">
          <Routing />
        </Box>
      </Box>
    </BrowserRouter>
  )
}

export default App
