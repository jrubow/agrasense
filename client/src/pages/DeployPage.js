import React from 'react'

function DeployPage() {
  return (
    <div className="deployPage">
      <h1>Optimize your deployment!</h1>
      <div className="userInput">
        <input type='text' placeholder='Enter Image URL'/>
        <button>Submit</button>
      </div>
    </div>
  )
}

export default DeployPage