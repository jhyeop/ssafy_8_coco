import { useSelector, useDispatch } from "react-redux";
import { onClickCompileButton } from "../../../store/toolBarActionSlice";
// import { onCompileSubmit } from "../../../store/compileSlice";

import IconButton from '@mui/material/IconButton';
import IntegrationInstructionsOutlinedIcon from '@mui/icons-material/IntegrationInstructionsOutlined';


function CompileButton(props) {
  const isCompileButtonOn = useSelector((state) => state.toolBarAction.isCompileButtonOn);
  const isCompilePossible = useSelector((state) => state.session.isCompilePossible);
  
  const dispatch = useDispatch();

  function handleOnClick() {
    if (isCompileButtonOn) {
      dispatch(onClickCompileButton());
    } else {
      dispatch(onClickCompileButton());
    }
  }

  return (
    <IconButton 
      onClick={handleOnClick} 
      isButtonOn={isCompileButtonOn}
      disabled={!isCompilePossible}
      sx={{ 
        width: "50px", 
        height: "50px", 
        m: '5px', 
        p: '5px', 
        bgcolor: isCompileButtonOn ? "#FCA311" : "#E5E5E5" 
      }}
    >
      <IntegrationInstructionsOutlinedIcon fontSize="large" />
    </IconButton>
  );
}

export default CompileButton;